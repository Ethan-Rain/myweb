package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.dto.ScanResultDTO;
import cn.helloworld1999.mediaservice.entity.Media;
import cn.helloworld1999.mediaservice.entity.MediaContent;
import cn.helloworld1999.mediaservice.mapper.MediaContentMapper;
import cn.helloworld1999.mediaservice.mapper.MediaMapper;
import cn.helloworld1999.mediaservice.service.FileScanner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class FileScannerImpl implements FileScanner {
    private static final Logger logger = LoggerFactory.getLogger(FileScanner.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final MediaMapper mediaMapper;
    private final MediaContentMapper mediaContentMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private static final Long DEFAULT_CATEGORY_ID = 2L;
    private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp");
    private static final Set<String> SUPPORTED_VIDEO_EXTENSIONS = Set.of(".mp4", ".avi", ".mkv", ".mov", ".wmv");
    private static final String MEDIA_KEY_PREFIX = "media:file:";
    private static final File POISON_PILL = new File("__POISON__");
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    @Autowired
    public FileScannerImpl(MediaMapper mediaMapper, MediaContentMapper mediaContentMapper, RedisTemplate<String, String> redisTemplate) {
        this.mediaMapper = mediaMapper;
        this.mediaContentMapper = mediaContentMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ScanResultDTO scanDirectory(String path, Long category, boolean useRedis) {
        ScanResultDTO result = new ScanResultDTO();
        ScanResultDTO.ScanStats stats = new ScanResultDTO.ScanStats();
        result.setStats(stats);
        result.setStoredFiles(new CopyOnWriteArrayList<>());
        result.setFailedFiles(new CopyOnWriteArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
        CountDownLatch latch = new CountDownLatch(THREAD_POOL_SIZE);

        try {
            Path rootPath = Paths.get(path);
            if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
                throw new IllegalArgumentException("路径不存在或不是目录: " + path);
            }

            logger.info("开始扫描目录: {}，线程数: {}", path, THREAD_POOL_SIZE);

            // 启动消费者线程
            for (int i = 0; i < THREAD_POOL_SIZE; i++) {
                executor.submit(() -> {
                    try {
                        while (true) {
                            File file = fileQueue.take();
                            if (file == POISON_PILL) break;
                            logger.debug("开始处理文件: {}，由线程: {}", file.getAbsolutePath(), Thread.currentThread().getName());
                            processFile(file, category, useRedis, result);
                        }
                    } catch (Exception e) {
                        logger.error("文件处理线程异常", e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 生产者遍历文件并放入队列
            logger.info("开始遍历文件树...");
            Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    logger.trace("发现文件: {}", file);
                    fileQueue.offer(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
            logger.info("文件遍历完毕，准备加入毒丸任务");

            for (int i = 0; i < THREAD_POOL_SIZE; i++) {
                fileQueue.offer(POISON_PILL);
            }

            latch.await();
            executor.shutdown();
            logger.info("文件扫描全部完成，总数: {}，成功: {}，失败: {}，跳过: {}" , stats.getTotalFiles(), stats.getSuccessCount(), stats.getFailureCount(), stats.getSkippedCount());
            result.setStatus("SUCCESS");
        } catch (Exception e) {
            logger.error("扫描目录失败: {}", path, e);
            result.setStatus("FAILURE");
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    private void processFile(File file, Long category, boolean useRedis, ScanResultDTO result) {
        ScanResultDTO.ScanStats stats = result.getStats();

        if (!file.exists() || !file.canRead() || file.isDirectory()) {
            stats.setSkippedCount(stats.getSkippedCount() + 1);
            logger.debug("跳过无效文件: {}", file.getAbsolutePath());
            return;
        }

        if (!isFileAccessible(file)) {
            stats.setSkippedCount(stats.getSkippedCount() + 1);
            logger.debug("文件不可访问或被锁定: {}", file.getAbsolutePath());
            return;
        }

        String extension = getFileExtension(file);
        if (extension == null) {
            stats.setUnsupportedFiles(stats.getUnsupportedFiles() + 1);
            logger.debug("不支持的文件类型: {}", file.getAbsolutePath());
            return;
        }

        if (file.length() <= 0) {
            stats.setEmptyFiles(stats.getEmptyFiles() + 1);
            logger.debug("空文件: {}", file.getAbsolutePath());
            return;
        }

        if (checkFileExists(file, useRedis)) {
            stats.setSkippedCount(stats.getSkippedCount() + 1);
            logger.debug("重复文件，跳过: {}", file.getAbsolutePath());
            return;
        }

        Media media = new Media();
        MediaContent mediaContent = new MediaContent();

        media.setMediaType(SUPPORTED_IMAGE_EXTENSIONS.contains(extension) ? "IMAGE" : "VIDEO");
        media.setTitle(file.getName());
        media.setDescription(file.getName());
        media.setUploadTime(LocalDateTime.now());
        media.setUpdateTime(LocalDateTime.now());
        media.setStatus("ACTIVE");
        media.setCategoryId(category != null ? category : DEFAULT_CATEGORY_ID);
        media.setSize(file.length());
        media.setUserId(1L);

        try {
            if (useRedis) {
                String key = MEDIA_KEY_PREFIX + file.getAbsolutePath();
                mediaMapper.insert(media);
                mediaContent.setId(media.getId() + 1);
                mediaContent.setMediaId(media.getId());
                mediaContent.setFilePath(file.getAbsolutePath());
                mediaContent.setFileName(file.getName());
                mediaContent.setFileExtension(extension);
                mediaContent.setStorageType("LOCAL");
                mediaContent.setStorageRegion("local");
                mediaContent.setWatermarkStatus(false);
                mediaContent.setTranscodeStatus("PENDING");

                ObjectNode mediaNode = objectMapper.valueToTree(media);
                ObjectNode contentNode = objectMapper.valueToTree(mediaContent);
                redisTemplate.opsForHash().put(key, "media", mediaNode.toString());
                redisTemplate.opsForHash().put(key, "content", contentNode.toString());
                logger.info("写入Redis成功: {}", file.getAbsolutePath());
            } else {
                mediaMapper.insert(media);
                mediaContent.setId(media.getId() + 1);
                mediaContent.setMediaId(media.getId());
                mediaContent.setFilePath(file.getAbsolutePath());
                mediaContent.setFileName(file.getName());
                mediaContent.setFileExtension(extension);
                mediaContent.setStorageType("LOCAL");
                mediaContent.setStorageRegion("local");
                mediaContent.setWatermarkStatus(false);
                mediaContent.setTranscodeStatus("PENDING");
                mediaContentMapper.insert(mediaContent);
                logger.info("写入MySQL成功: {}", file.getAbsolutePath());
            }

            stats.setTotalFiles(stats.getTotalFiles() + 1);
            stats.setSuccessCount(stats.getSuccessCount() + 1);
            Map<String, Object> stored = new HashMap<>();
            stored.put("filePath", file.getAbsolutePath());
            stored.put("mediaType", media.getMediaType());
            stored.put("storageType", useRedis ? "Redis" : "MySQL");
            result.getStoredFiles().add(stored);
        } catch (Exception e) {
            stats.setFailureCount(stats.getFailureCount() + 1);
            result.getFailedFiles().add(file.getAbsolutePath());
            logger.error("处理文件失败: {}", file.getAbsolutePath(), e);
        }
    }

    private boolean isFileAccessible(File file) {
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            channel.size();
            return true;
        } catch (Exception e) {
            logger.debug("文件无法访问: {}", file.getAbsolutePath());
            return false;
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < name.length() - 1) {
            String ext = name.substring(lastDot).toLowerCase();
            if (SUPPORTED_IMAGE_EXTENSIONS.contains(ext) || SUPPORTED_VIDEO_EXTENSIONS.contains(ext)) {
                return ext;
            }
        }
        return null;
    }

    private boolean checkFileExists(File file, boolean useRedis) {
        if (useRedis) {
            return Boolean.TRUE.equals(redisTemplate.hasKey(MEDIA_KEY_PREFIX + file.getAbsolutePath()));
        } else {
            QueryWrapper<MediaContent> query = new QueryWrapper<>();
            query.eq("file_path", file.getAbsolutePath());
            return mediaContentMapper.selectCount(query) > 0;
        }
    }
}