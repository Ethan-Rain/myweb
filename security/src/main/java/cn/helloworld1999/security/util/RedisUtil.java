package cn.helloworld1999.security.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis工具类，用于简化Redis操作
 * 提供了字符串类型数据的存储、获取、删除和过期时间设置等功能
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置指定键的值
     *
     * @param key 键，用于唯一标识存储的数据
     * @param value 值，存储的数据内容
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取指定键的值
     *
     * @param key 键，用于唯一标识存储的数据
     * @return 存储的字符串数据，如果键不存在则返回null
     */
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定键及其对应的值
     *
     * @param key 键，用于唯一标识存储的数据
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 检查给定键是否存在
     *
     * @param key 键，用于唯一标识存储的数据
     * @return 如果键存在返回true，否则返回false
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取指定键的过期时间
     *
     * @param key 键，用于唯一标识存储的数据
     * @return 过期时间，单位为秒；如果键不存在或没有设置过期时间，则返回-1
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 设置指定键的过期时间
     *
     * @param key 键，用于唯一标识存储的数据
     * @param timeout 过期时间，单位为秒
     */
    public void setExpire(String key, long timeout) {
        redisTemplate.expire(key, timeout, java.util.concurrent.TimeUnit.SECONDS);
    }

}

