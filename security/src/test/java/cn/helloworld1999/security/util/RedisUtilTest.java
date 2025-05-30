package cn.helloworld1999.security.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void set() {
        String key = "test";
        String value = "hello world";
        redisTemplate.opsForValue().set(key, value);
    }
    @Test
    public void get() {
        String key = "test";
        System.out.println(((String) redisTemplate.opsForValue().get(key)));
    }
}