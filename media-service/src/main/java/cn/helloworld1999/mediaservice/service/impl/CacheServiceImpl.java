package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
    @Autowired
    private MediaService mediaService;
    @Override
    public Map<String, Object> syncToRedis() {
        System.out.println("缓存同步开始");
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