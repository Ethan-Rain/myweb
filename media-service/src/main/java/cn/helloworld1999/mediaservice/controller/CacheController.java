package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/sync")
    public Map<String, Object> syncToRedis() {
        return cacheService.syncToRedis();
    }
/*    @GetMapping("/syncV2")
    public Map<String, Object> syncToRedisV2() {
            CacheServiceImplV2 cacheServiceV2 = new CacheServiceImplV2();
        return cacheServiceV2.syncToRedis();
    }*/
}