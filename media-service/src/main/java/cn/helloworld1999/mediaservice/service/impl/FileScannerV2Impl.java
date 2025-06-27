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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件扫描服务实现类（优化版）
 */
@Service
public class FileScannerV2Impl implements FileScanner {
    private static final Logger logger = LoggerFactory.getLogger(FileScannerImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 线程池配置
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final int QUEUE_CAPACITY = 1000;
    private static final int BATCH_SIZE = 100;

    private final ExecutorService executorService = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Autowired
    private MediaMapper mediaMapper;

    @Autowired
    private MediaContentMapper mediaContentMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private static final Long DEFAULT_CATEGORY_ID = 2L;

    private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
    );

    private static final Set<String> SUPPORTED_VIDEO_EXTENSIONS = Set.of(
            ".mp4", ".avi", ".mkv", ".mov", ".wmv"
    );

    private static final String MEDIA_KEY_PREFIX = "media:file:";

    // 用于缓存已处理的文件路径
    private final Set<String> processedPaths = ConcurrentHashMap.newKeySet();

    @Override
    @Transactional
    public ScanResultDTO scanDirectory(String path, Long category, boolean useRedis) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Path directory = Paths.get(path);
            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                throw new IllegalArgumentException("指定的路径不存在或不是目录: " + path);
            }

            logger.info("开始扫描目录: {}", path);
            ScanResultDTO result = new ScanResultDTO();
            ScanResultDTO.ScanStats stats = new ScanResultDTO.ScanStats();
            result.setStats(stats);
            result.setFailedFiles(Collections.synchronizedList(new ArrayList<>()));
            result.setStoredFiles(Collections.synchronizedList(new ArrayList<>()));

            // 清空已处理路径缓存
            processedPaths.clear();

            // 获取所有文件路径
            List<Path> allFiles;
            try (Stream<Path> walk = Files.walk(directory)) {
                allFiles = walk
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
            }

            // 批量处理文件
            processFilesInBatches(allFiles, category, useRedis, result);

            stopWatch.stop();
            logger.info("目录扫描完成: {}, 耗时: {} 秒", path, stopWatch.getTotalTimeSeconds());

            result.setStatus("SUCCESS");
            return result;
        } catch (Exception e) {
            logger.error("扫描目录失败: {}", path, e);
            ScanResultDTO result = new ScanResultDTO();
            result.setStatus("FAILURE");
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    private void processFilesInBatches(List<Path> files, Long category, boolean useRedis, ScanResultDTO result) {
        // 分批处理文件
        int totalFiles = files.size();
        result.getStats().setTotalFiles(totalFiles);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Path> batch = new ArrayList<>(BATCH_SIZE);

        for (Path file : files) {
            batch.add(file);

            if (batch.size() >= BATCH_SIZE) {
                List<Path> currentBatch = new ArrayList<>(batch);
                futures.add(processBatchAsync(currentBatch, category, useRedis, result));
                batch.clear();
            }
        }

        // 处理最后一批
        if (!batch.isEmpty()) {
            futures.add(processBatchAsync(batch, category, useRedis, result));
        }

        // 等待所有批次完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Async
    public CompletableFuture<Void> processBatchAsync(List<Path> batch, Long category, boolean useRedis, ScanResultDTO result) {
        return CompletableFuture.runAsync(() -> {
            for (Path filePath : batch) {
                try {
                    processFile(filePath.toFile(), category, useRedis, result);
                } catch (Exception e) {
                    logger.error("处理文件失败: " + filePath, e);
                    result.getFailedFiles().add(filePath + ": " + e.getMessage());
                    result.getStats().setFailureCount(result.getStats().getFailureCount() + 1);
                }
            }
        }, executorService);
    }

    private void processFile(File file, Long category, boolean useRedis, ScanResultDTO result) {
        String filePath = file.getAbsolutePath();

        // 检查是否已处理过
        if (!processedPaths.add(filePath)) {
            result.getStats().setSkippedCount(result.getStats().getSkippedCount() + 1);
            return;
        }

        String extension = getFileExtension(file);
        if (extension == null) {
            result.getStats().setUnsupportedFiles(result.getStats().getUnsupportedFiles() + 1);
            return;
        }

        if (file.length() <= 0) {
            result.getStats().setEmptyFiles(result.getStats().getEmptyFiles() + 1);
            return;
        }

        // 批量检查文件是否存在
        if (checkFileExists(file, useRedis)) {
            result.getStats().setSkippedCount(result.getStats().getSkippedCount() + 1);
            return;
        }

        try {
            Media media = createMedia(file, extension, category);
            MediaContent content = createMediaContent(file, extension, media.getId());

            if (useRedis) {
                storeInRedis(file, media, content);
            } else {
                storeInDatabase(media, content);
            }
            // @TODO 暂时不保存文件信息了
            result.getStoredFiles().add(null);
            result.getStats().setSuccessCount(result.getStats().getSuccessCount() + 1);
        } catch (Exception e) {
            logger.error("处理文件失败: " + filePath, e);
            result.getFailedFiles().add(filePath + ": " + e.getMessage());
            result.getStats().setFailureCount(result.getStats().getFailureCount() + 1);
        }
    }

    private Media createMedia(File file, String extension, Long category) {
        Media media = new Media();
        media.setMediaType(isImageExtension(extension) ? "IMAGE" : "VIDEO");
        media.setTitle(file.getName());
        media.setDescription(file.getName());
        media.setUploadTime(LocalDateTime.now());
        media.setUpdateTime(LocalDateTime.now());
        media.setStatus("ACTIVE");
        media.setCategoryId(category != null ? category : DEFAULT_CATEGORY_ID);
        media.setSize(file.length());
        media.setUserId(1L); // TODO: 获取当前登录用户ID
        return media;
    }

    private MediaContent createMediaContent(File file, String extension, Long mediaId) {
        MediaContent content = new MediaContent();
        content.setMediaId(mediaId);
        content.setFilePath(file.getAbsolutePath());
        content.setFileName(file.getName());
        content.setFileExtension(extension);
        content.setStorageType("LOCAL");
        content.setStorageRegion("local");
        content.setWatermarkStatus(false);
        content.setTranscodeStatus("PENDING");
        return content;
    }

    private void storeInRedis(File file, Media media, MediaContent content){
        mediaMapper.insert(media);
        content.setMediaId(media.getId());

        String key = MEDIA_KEY_PREFIX + file.getAbsolutePath();
        ObjectNode mediaNode = objectMapper.valueToTree(media);
        ObjectNode contentNode = objectMapper.valueToTree(content);

        redisTemplate.opsForHash().put(key, "media", mediaNode.toString());
        redisTemplate.opsForHash().put(key, "content", contentNode.toString());
    }

    private void storeInDatabase(Media media, MediaContent content) {
        mediaMapper.insert(media);
        content.setMediaId(media.getId());
        mediaContentMapper.insert(content);
    }

    private boolean checkFileExists(File file, boolean useRedis) {
        if (useRedis) {
            String key = MEDIA_KEY_PREFIX + file.getAbsolutePath();
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        }

        // 使用批量查询优化数据库查询
        QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("1").eq("file_path", file.getAbsolutePath()).last("LIMIT 1");
        return mediaContentMapper.selectCount(queryWrapper) > 0;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            return null;
        }
        String extension = name.substring(lastDot).toLowerCase();
        if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension) ||
                SUPPORTED_VIDEO_EXTENSIONS.contains(extension)) {
            return extension;
        }
        return null;
    }

    private boolean isImageExtension(String extension) {
        return SUPPORTED_IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }
}