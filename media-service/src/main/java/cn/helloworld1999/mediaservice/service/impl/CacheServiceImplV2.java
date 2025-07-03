package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.dto.RedisMediaHashDTO;
import cn.helloworld1999.mediaservice.mapper.MediaHashMapper;
import cn.helloworld1999.mediaservice.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheServiceImplV2{
    @Autowired
    MediaHashMapper mediaHashMapper;
    @Autowired
    RedisUtil redisUtil;


    public Map<String, Object> syncToRedis() {
        return getAllMediaFromDatabase(null);
    }


    public Map<String, Object> syncToCaffeineCache() {
        return Map.of();
    }


    public Map<String, Object> getAllMediaFromDatabase(Long category) {
        category = category == null ? 1L : category;
        List<RedisMediaHashDTO> redisMediaHashDTOS = mediaHashMapper.findMediaByCategory(category);
        Map<String, Object> map = new HashMap<>();
        for (RedisMediaHashDTO redisMediaHashDTO : redisMediaHashDTOS) {
            map.put(redisMediaHashDTO.getId().toString(), redisMediaHashDTO);
        }
        redisUtil.hmset("cache:images", map);
        return Map.of();
    }
}