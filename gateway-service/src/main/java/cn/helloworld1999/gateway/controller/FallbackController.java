package cn.helloworld1999.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关测试和降级控制器
 */
@RestController
public class FallbackController {

    /**
     * 网关健康检查接口
     */
    @GetMapping("/gateway/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "Gateway is running");
        result.put("timestamp", System.currentTimeMillis());
        return Mono.just(result);
    }

    /**
     * 服务降级默认响应
     */
    @GetMapping("/fallback")
    public Mono<Map<String, Object>> fallback() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "服务暂时不可用，请稍后再试");
        result.put("timestamp", System.currentTimeMillis());
        return Mono.just(result);
    }
}
