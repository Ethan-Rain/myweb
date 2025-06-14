package cn.helloworld1999.mediaservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface CacheService {
    /**
     * 同步缓存到Redis
     */
    Map<String, Object> syncToRedis();
}