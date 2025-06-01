package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/random")
public class RandomMediaController {

    private static final Logger logger = LoggerFactory.getLogger(RandomMediaController.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/image")
    public Map<String, Object> getRandomImage(@RequestParam String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 从Redis缓存中获取图片列表
            List<String> cachedImages = (List<String>) redisTemplate.opsForValue().get("cache:images");
            if (cachedImages == null || cachedImages.isEmpty()) {
                // 如果缓存不存在，从MySQL获取
                List<String> randomImages = mediaService.getRandomImages(category);
                redisTemplate.opsForValue().set("cache:images", randomImages, 1, TimeUnit.HOURS);
                cachedImages = randomImages;
            }
            
            // 随机选择一张图片
            if (!cachedImages.isEmpty()) {
                int randomIndex = new Random().nextInt(cachedImages.size());
                result.put("image", cachedImages.get(randomIndex));
            } else {
                result.put("error", "没有找到图片资源");
            }
        } catch (Exception e) {
            logger.error("获取随机图片失败", e);
            result.put("error", "获取随机图片失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/video")
    public Map<String, Object> getRandomVideo(@RequestParam String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 从Redis缓存中获取视频列表
            List<String> cachedVideos = (List<String>) redisTemplate.opsForValue().get("cache:videos");
            if (cachedVideos == null || cachedVideos.isEmpty()) {
                // 如果缓存不存在，从MySQL获取
                List<String> randomVideos = mediaService.getRandomVideos(category);
                redisTemplate.opsForValue().set("cache:videos", randomVideos, 1, TimeUnit.HOURS);
                cachedVideos = randomVideos;
            }
            
            // 随机选择一个视频
            if (!cachedVideos.isEmpty()) {
                int randomIndex = new Random().nextInt(cachedVideos.size());
                result.put("video", cachedVideos.get(randomIndex));
            } else {
                result.put("error", "没有找到视频资源");
            }
        } catch (Exception e) {
            logger.error("获取随机视频失败", e);
            result.put("error", "获取随机视频失败: " + e.getMessage());
        }
        return result;
    }


}