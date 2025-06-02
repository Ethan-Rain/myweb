package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/cache")
public class CacheController {
    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存所有资源数据到Redis
     * @return 缓存结果
     */
    @PostMapping("/sync")
    public Map<String, Object> syncToRedis() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取所有图片
            List<String> allImages = mediaService.getAllImages(1L);
            logger.info("获取到图片数量: {}", allImages.size());
            redisTemplate.opsForValue().set("cache:images", allImages, 24, TimeUnit.HOURS);
            
            // 获取所有视频
            List<String> allVideos = mediaService.getAllVideos(1L);
            logger.info("获取到视频数量: {}", allVideos.size());
            redisTemplate.opsForValue().set("cache:videos", allVideos, 24, TimeUnit.HOURS);
            
            result.put("success", true);
            result.put("message", "缓存同步成功");
        } catch (Exception e) {
            logger.error("缓存同步失败", e);
            result.put("success", false);
            result.put("message", "缓存同步失败: " + e.getMessage());
        }
        return result;
    }
}
