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
import java.time.LocalDateTime;
import java.util.*;

/**
 * 文件扫描服务实现类
 */
@Service
public class FileScannerImpl implements FileScanner {
    private static final Logger logger = LoggerFactory.getLogger(FileScanner.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 使用构造函数注入替代字段注入
    private final MediaMapper mediaMapper;
    private final MediaContentMapper mediaContentMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public FileScannerImpl(MediaMapper mediaMapper, MediaContentMapper mediaContentMapper, RedisTemplate<String, String> redisTemplate) {
        this.mediaMapper = mediaMapper;
        this.mediaContentMapper = mediaContentMapper;
        this.redisTemplate = redisTemplate;
    }
    /**
     * 默认分类ID
     */
    private static final Long DEFAULT_CATEGORY_ID = 2L;

    /**
     * 支持的文件类型
     */
    private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
    ));

    private static final Set<String> SUPPORTED_VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".mp4", ".avi", ".mkv", ".mov", ".wmv"
    ));

    /**
     * Redis key前缀
     */
    private static final String MEDIA_KEY_PREFIX = "media:file:";

    /**
     * 递归扫描指定路径下的所有文件
     * @param path 要扫描的路径
     * @param category 分类ID
     * @param useRedis 是否使用Redis存储
     * @return 扫描结果
     */
    @Override
    public ScanResultDTO scanDirectory(String path, Long category, boolean useRedis) {
        try {
            File directory = new File(path);
            if (!directory.exists() || !directory.isDirectory()) {
                throw new IllegalArgumentException("指定的路径不存在或不是目录: " + path);
            }

            logger.info("开始扫描目录: {}", path);
            ScanResultDTO result = new ScanResultDTO();
            ScanResultDTO.ScanStats stats = new ScanResultDTO.ScanStats();
            stats.setTotalFiles(0);
            stats.setSuccessCount(0);
            stats.setFailureCount(0);
            stats.setSkippedCount(0);
            stats.setEmptyFiles(0);
            stats.setUnsupportedFiles(0);
            result.setStats(stats);
            result.setFailedFiles(new ArrayList<>());
            result.setStoredFiles(new ArrayList<>());

            scanDirectoryRecursively(directory, category, useRedis, result);
            logger.info("目录扫描完成: {}", path);
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

    private void scanDirectoryRecursively(File directory, Long category, boolean useRedis, ScanResultDTO result) {
        try {
            File[] files = directory.listFiles();
            if (files == null) {
                logger.warn("目录为空: {}", directory.getAbsolutePath());
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    logger.debug("扫描子目录: {}", file.getAbsolutePath());
                    scanDirectoryRecursively(file, category, useRedis, result);
                } else {
                    processFile(file, category, useRedis, result);
                }
            }
        } catch (Exception e) {
            logger.error("扫描目录失败: {}", directory.getAbsolutePath(), e);
            throw e;
        }
    }

    private void processFile(File file, Long category, boolean useRedis, ScanResultDTO result) {
        try {
            logger.debug("处理文件: {}", file.getAbsolutePath());
            
            String extension = getFileExtension(file);
            if (extension == null) {
                logger.debug("跳过非支持文件: {}", file.getAbsolutePath());
                result.getStats().setUnsupportedFiles(result.getStats().getUnsupportedFiles() + 1);
                return;
            }

            // 检查文件大小
            if (file.length() <= 0) {
                logger.warn("跳过空文件: {}", file.getAbsolutePath());
                result.getStats().setEmptyFiles(result.getStats().getEmptyFiles() + 1);
                return;
            }

            // 检查是否已存在
            if (checkFileExists(file, useRedis)) {
                logger.debug("跳过已存在文件: {}", file.getAbsolutePath());
                result.getStats().setSkippedCount(result.getStats().getSkippedCount() + 1);
                return;
            }

            Media media = new Media();
            MediaContent mediaContent = new MediaContent();

            // 设置基础信息
            media.setMediaType(isImageExtension(extension) ? "IMAGE" : "VIDEO");
            media.setTitle(file.getName());
            media.setDescription(file.getName());
            media.setUploadTime(LocalDateTime.now());
            media.setUpdateTime(LocalDateTime.now());
            media.setStatus("ACTIVE");
            
            // 设置分类ID
            category = category != null ? category : DEFAULT_CATEGORY_ID;
            media.setCategoryId(category);
            
            media.setSize(file.length());
            media.setUserId(1L); // TODO: 获取当前登录用户ID

            if (useRedis) {
                // 使用Redis存储
                try {
                    String key = MEDIA_KEY_PREFIX + file.getAbsolutePath();
                    // 保存媒体信息，使用数据库自增ID
                    mediaMapper.insert(media);
                    
                    // 设置内容信息，使用媒体ID
                    mediaContent.setId(media.getId() + 1); // 确保内容ID与媒体ID不同但有关联
                    mediaContent.setMediaId(media.getId());
                    mediaContent.setFilePath(file.getAbsolutePath());
                    mediaContent.setFileName(file.getName());
                    mediaContent.setFileExtension(extension);
                    mediaContent.setStorageType("LOCAL");
                    mediaContent.setStorageRegion("local");
                    mediaContent.setWatermarkStatus(false);
                    mediaContent.setTranscodeStatus("PENDING");

                    // 使用JSON序列化
                    ObjectNode mediaNode = objectMapper.valueToTree(media);
                    ObjectNode contentNode = objectMapper.valueToTree(mediaContent);
                    redisTemplate.opsForHash().put(key, "media", mediaNode.toString());
                    redisTemplate.opsForHash().put(key, "content", contentNode.toString());
                    logger.info("存储到Redis成功: {}", file.getAbsolutePath());
                    
                    // 记录成功信息
                } catch (Exception e) {
                    logger.error("存储到Redis失败: {}", file.getAbsolutePath(), e);
                    result.getFailedFiles().add(file.getAbsolutePath());
                    result.getStats().setFailureCount(result.getStats().getFailureCount() + 1);
                    return;
                }
            } else {
                // 使用MySQL存储
                try {
                    // 保存媒体信息，使用数据库自增ID
                    mediaMapper.insert(media);
                    
                    // 设置内容信息，使用媒体ID
                    mediaContent.setId(media.getId() + 1); // 确保内容ID与媒体ID不同但有关联
                    mediaContent.setMediaId(media.getId());
                    mediaContent.setFilePath(file.getAbsolutePath());
                    mediaContent.setFileName(file.getName());
                    mediaContent.setFileExtension(extension);
                    mediaContent.setStorageType("LOCAL");
                    mediaContent.setStorageRegion("local");
                    mediaContent.setWatermarkStatus(false);
                    mediaContent.setTranscodeStatus("PENDING");

                    // 保存内容信息
                    mediaContentMapper.insert(mediaContent);
                    logger.info("存储到MySQL成功: {}", file.getAbsolutePath());
                    
                    // 记录成功信息
                    result.getStats().setSuccessCount(result.getStats().getSuccessCount() + 1);
                    Map<String, Object> storedFile = new HashMap<>();
                    storedFile.put("filePath", file.getAbsolutePath());
                    storedFile.put("mediaType", media.getMediaType());
                    storedFile.put("storageType", "MySQL");
                    result.getStoredFiles().add(storedFile);
                } catch (Exception e) {
                    logger.error("存储到MySQL失败: {}", file.getAbsolutePath(), e);
                    result.getStats().setFailureCount(result.getStats().getFailureCount() + 1);
                    result.getFailedFiles().add(file.getAbsolutePath());
                    throw e;
                }
            }
            
            result.getStats().setTotalFiles(result.getStats().getTotalFiles() + 1);
        } catch (Exception e) {
            logger.error("处理文件失败: {}", file.getAbsolutePath(), e);
            result.getStats().setFailureCount(result.getStats().getFailureCount() + 1);
            result.getFailedFiles().add(file.getAbsolutePath());
            
            // 如果是分类相关错误，记录具体原因
            if (e.getMessage() != null && e.getMessage().contains("category_id")) {
                result.setErrorMessage("指定的分类不存在，请先创建分类");
            }
            throw e;
        }
    }

    private String getFileExtension(File file) {
        try {
            String name = file.getName();
            int lastDotIndex = name.lastIndexOf('.');
            if (lastDotIndex >= 0 && lastDotIndex < name.length() - 1) {
                String extension = name.substring(lastDotIndex).toLowerCase();
                if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension) || SUPPORTED_VIDEO_EXTENSIONS.contains(extension)) {
                    return extension;
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("获取文件扩展名失败: {}", file.getAbsolutePath(), e);
            throw e;
        }
    }

    private boolean isImageExtension(String extension) {
        return SUPPORTED_IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 检查文件是否已存在
     * @param file 文件
     * @param useRedis 是否使用Redis
     * @return 是否已存在
     */
    private boolean checkFileExists(File file, boolean useRedis) {
        try {
            if (useRedis) {
                String key = MEDIA_KEY_PREFIX + file.getAbsolutePath();
                return Boolean.TRUE.equals(redisTemplate.hasKey(key));
            } else {
                // MySQL检查
                QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("file_path", file.getAbsolutePath());
                return mediaContentMapper.selectCount(queryWrapper) > 0;
            }
        } catch (Exception e) {
            logger.error("检查文件是否存在失败: {}", file.getAbsolutePath(), e);
            return false;
        }
    }
}
