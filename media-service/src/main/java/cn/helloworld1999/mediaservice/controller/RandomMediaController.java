package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.service.MediaService;
import cn.helloworld1999.mediaservice.config.FileConfig;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/random")
public class RandomMediaController {

    private static final Logger logger = LoggerFactory.getLogger(RandomMediaController.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FileConfig fileConfig;
    
    private String getMimeType(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "ogg":
            case "ogv":
                return "video/ogg";
            default:
                return "application/octet-stream";
        }
    }

    @GetMapping("/image")
    public ResponseEntity<?> getRandomImage(
            @RequestParam(value = "category", required = false, defaultValue = "1") Long category) {
        
        logger.info("开始处理获取随机图片请求，分类ID: {}", category);
        
        try {
            // 获取随机图片
            logger.info("正在从服务层获取随机图片...");
            List<String> randomImages = mediaService.getRandomImages(category);
            logger.info("从服务层获取到随机图片列表: {}", randomImages);
            
            if (randomImages == null || randomImages.isEmpty()) {
                String errorMsg = "未找到图片，分类ID: " + category;
                logger.warn(errorMsg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", errorMsg));
            }
            
            // 获取随机图片路径
            String imagePath = randomImages.get(0);
            logger.info("获取到随机图片路径: {}", imagePath);
            
            if (fileConfig == null) {
                String errorMsg = "FileConfig 未注入";
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", errorMsg));
            }
            
            // 构建完整文件路径
            String baseDir = fileConfig.getBaseDir();
            // 处理路径分隔符问题
            String normalizedImagePath = imagePath.replace("\\", "/");
            if (normalizedImagePath.startsWith("/")) {
                normalizedImagePath = normalizedImagePath.substring(1);
            }
            String fullPath = baseDir + "/" + normalizedImagePath;
            
            logger.info("基础目录: {}", baseDir);
            logger.info("处理后的相对路径: {}", normalizedImagePath);
            logger.info("完整文件路径: {}", fullPath);
            
            // 检查文件是否存在
            File imageFile = new File(fullPath);
            
            if (!imageFile.exists()) {
                // 尝试直接使用路径（不拼接baseDir）
                File altFile = new File(imagePath);
                if (altFile.exists()) {
                    imageFile = altFile;
                    logger.info("使用相对路径找到文件: {}", imagePath);
                } else {
                    String errorMsg = String.format("图片文件不存在: %s (当前工作目录: %s)", 
                        fullPath, System.getProperty("user.dir"));
                    logger.error(errorMsg);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", errorMsg));
                }
            }
            
            if (!imageFile.canRead()) {
                String errorMsg = String.format("图片文件不可读，请检查权限: %s", imageFile.getAbsolutePath());
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", errorMsg));
            }
            
            // 使用Resource接口进行流式传输
            Resource resource = new FileSystemResource(imageFile);
            String mimeType = getMimeType(imageFile.getName());
            logger.info("检测到MIME类型: {}", mimeType);
            
            // 构建响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentLength(imageFile.length());
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(imageFile.getName(), java.nio.charset.StandardCharsets.UTF_8)
                    .build());
            
            logger.info("准备流式传输图片文件，大小: {} 字节", imageFile.length());
            
            // 返回流式响应
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            String errorMsg = "获取随机图片失败: " + e.getMessage();
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", errorMsg));
        }
    }

    @GetMapping("/video")
    public ResponseEntity<?> getRandomVideo(
            @RequestParam(value = "category", required = false, defaultValue = "1") Long category) {
        
        logger.info("开始处理获取随机视频请求，分类ID: {}", category);
        
        try {
            // 获取随机视频
            logger.info("正在从服务层获取随机视频...");
            List<String> randomVideos = mediaService.getRandomVideos(category);
            logger.info("从服务层获取到随机视频列表: {}", randomVideos);
            
            if (randomVideos == null || randomVideos.isEmpty()) {
                String errorMsg = "未找到视频，分类ID: " + category;
                logger.warn(errorMsg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", errorMsg));
            }
            
            // 获取随机视频路径
            String videoPath = randomVideos.get(0);
            logger.info("获取到随机视频路径: {}", videoPath);
            
            if (fileConfig == null) {
                String errorMsg = "FileConfig 未注入";
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", errorMsg));
            }
            
            // 构建完整文件路径
            String baseDir = fileConfig.getBaseDir();
            // 处理路径分隔符问题
            String normalizedVideoPath = videoPath.replace("\\", "/");
            if (normalizedVideoPath.startsWith("/")) {
                normalizedVideoPath = normalizedVideoPath.substring(1);
            }
            String fullPath = baseDir + "/" + normalizedVideoPath;
            
            logger.info("基础目录: {}", baseDir);
            logger.info("处理后的相对路径: {}", normalizedVideoPath);
            logger.info("完整文件路径: {}", fullPath);
            
            // 检查文件是否存在
            File videoFile = new File(fullPath);
            
            if (!videoFile.exists()) {
                // 尝试直接使用路径（不拼接baseDir）
                File altFile = new File(videoPath);
                if (altFile.exists()) {
                    videoFile = altFile;
                    logger.info("使用相对路径找到文件: {}", videoPath);
                } else {
                    String errorMsg = String.format("视频文件不存在: %s (当前工作目录: %s)", 
                        fullPath, System.getProperty("user.dir"));
                    logger.error(errorMsg);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", errorMsg));
                }
            }
            
            if (!videoFile.canRead()) {
                String errorMsg = String.format("视频文件不可读，请检查权限: %s", videoFile.getAbsolutePath());
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", errorMsg));
            }
            
            // 使用Resource接口进行流式传输
            Resource resource = new FileSystemResource(videoFile);
            String mimeType = getMimeType(videoFile.getName());
            logger.info("检测到MIME类型: {}", mimeType);
            
            // 构建响应头，支持视频范围请求（用于视频播放器）
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentLength(videoFile.length());
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(videoFile.getName(), java.nio.charset.StandardCharsets.UTF_8)
                    .build());
            
            // 支持范围请求（Range requests）
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            
            logger.info("准备流式传输视频文件，大小: {} 字节", videoFile.length());
            
            // 返回流式响应
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            String errorMsg = "获取随机视频失败: " + e.getMessage();
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", errorMsg));
        }
    }


}