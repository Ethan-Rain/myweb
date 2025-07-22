package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.config.FileConfig;
import cn.helloworld1999.mediaservice.service.impl.MediaService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取指定分类下的随机图片文件流
     *
     * @param category 图片分类ID（可选参数，默认值为1）
     * @return ResponseEntity 包含以下可能响应：
     * - 200 OK 携带图片资源流
     * - 404 NOT_FOUND 当未找到图片或文件不存在
     * - 403 FORBIDDEN 当文件不可读
     * - 500 INTERNAL_SERVER_ERROR 配置错误或处理异常
     */
    @GetMapping("/image")
    public ResponseEntity<?> getRandomImage(
            @RequestParam(value = "category", required = false, defaultValue = "1") Long category) {

        logger.info("开始处理获取随机图片请求，分类ID: {}", category);

        try {
            // 从服务层获取随机图片列表
            logger.info("正在从服务层获取随机图片...");
            List<String> randomImages = mediaService.getRandomImages(category);
            logger.info("从服务层获取到随机图片列表: {}", randomImages);

            // 处理空结果集情况
            if (randomImages == null || randomImages.isEmpty()) {
                String errorMsg = "未找到图片，分类ID: " + category;
                logger.warn(errorMsg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", errorMsg));
            }

            // 获取首个随机图片路径
            String imagePath = randomImages.get(0);
            logger.info("获取到随机图片路径: {}", imagePath);

            // 检查文件配置注入状态
            if (fileConfig == null) {
                String errorMsg = "FileConfig 未注入";
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", errorMsg));
            }

            // 构建规范化文件路径
            String baseDir = fileConfig.getBaseDir();
            String normalizedImagePath = imagePath.replace("\\", "/");
            if (normalizedImagePath.startsWith("/")) {
                normalizedImagePath = normalizedImagePath.substring(1);
            }
            String fullPath = baseDir + "/" + normalizedImagePath;

            logger.info("基础目录: {}", baseDir);
            logger.info("处理后的相对路径: {}", normalizedImagePath);
            logger.info("完整文件路径: {}", fullPath);

            // 双路径检查策略：优先完整路径，其次尝试相对路径
            File imageFile = new File(fullPath);
            if (!imageFile.exists()) {
                File altFile = new File(imagePath);
                if (altFile.exists()) {
                    imageFile = altFile;
                    logger.info("使用相对路径找到文件: {}", imagePath);
                    logger.info("文件名称: {}", imageFile.getName());
                } else {
                    String errorMsg = String.format("图片文件不存在: %s (当前工作目录: %s)",
                            fullPath, System.getProperty("user.dir"));
                    logger.error(errorMsg);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", errorMsg));
                }
            }

            // 文件权限校验
            if (!imageFile.canRead()) {
                String errorMsg = String.format("图片文件不可读，请检查权限: %s", imageFile.getAbsolutePath());
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", errorMsg));
            }

            // 构建流式响应资源
            Resource resource = new FileSystemResource(imageFile);
            String mimeType = getMimeType(imageFile.getName());
            logger.info("检测到MIME类型: {}", mimeType);

            // 设置响应头信息
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentLength(imageFile.length());
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(imageFile.getName(), java.nio.charset.StandardCharsets.UTF_8)
                    .build());

            logger.info("准备流式传输图片文件，大小: {} 字节", imageFile.length());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (Exception e) {
            // 统一异常处理
            String errorMsg = "获取随机图片失败: " + e.getMessage();
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", errorMsg));
        }
    }


    @GetMapping("/video")
    public ResponseEntity<byte[]> getRandomVideo(
            @RequestParam(value = "category", required = false, defaultValue = "1") Long category,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

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
                        .body(Collections.singletonMap("error", errorMsg).toString().getBytes());
            }

            // 获取随机视频路径
            String videoPath = randomVideos.get(0);
            logger.info("获取到随机视频路径: {}", videoPath);

            if (fileConfig == null) {
                String errorMsg = "FileConfig 未注入";
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", errorMsg).toString().getBytes());
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
                            .body(Collections.singletonMap("error", errorMsg).toString().getBytes());
                }
            }

            if (!videoFile.canRead()) {
                String errorMsg = String.format("视频文件不可读，请检查权限: %s", videoFile.getAbsolutePath());
                logger.error(errorMsg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", errorMsg).toString().getBytes());
            }

            long fileLength = videoFile.length();
            byte[] buffer = new byte[(int) fileLength];
            try (java.io.FileInputStream fis = new java.io.FileInputStream(videoFile)) {
                fis.read(buffer);
            }

            String mimeType = getMimeType(videoFile.getName());
            logger.info("检测到MIME类型: {}", mimeType);

            // 构建响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(videoFile.getName(), java.nio.charset.StandardCharsets.UTF_8)
                    .build());

            // 支持范围请求（Range requests）
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

            // 处理范围请求
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String rangeValue = rangeHeader.replace("bytes=", "");
                long start = 0;
                long end = fileLength - 1;

                if (rangeValue.contains("-")) {
                    String[] ranges = rangeValue.split("-");
                    start = Long.parseLong(ranges[0]);
                    if (ranges.length > 1) {
                        end = Long.parseLong(ranges[1]);
                    }
                }

                // 确保范围有效
                if (start < 0) start = 0;
                if (end >= fileLength) end = fileLength - 1;
                if (start > end) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
                }

                long contentLength = end - start + 1;
                headers.setContentLength(contentLength);
                headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);

                // 返回部分内容
                byte[] partialContent = new byte[(int) contentLength];
                System.arraycopy(buffer, (int) start, partialContent, 0, (int) contentLength);

                logger.info("处理范围请求: {}-{}/{}", start, end, fileLength);
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(partialContent);
            }

            // 返回完整文件
            headers.setContentLength(fileLength);
            logger.info("返回完整视频文件，大小: {} 字节", fileLength);
            return new ResponseEntity<>(buffer, headers, HttpStatus.OK);

        } catch (Exception e) {
            String errorMsg = "获取随机视频失败: " + e.getMessage();
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/imageByURL")
    public ResponseEntity<?> getRandomImageInfo(@RequestBody Map<String,Object> queryInfo) {
        return ResponseEntity.ok(mediaService.getRandomMediaInfoJSON(queryInfo));
    }
}