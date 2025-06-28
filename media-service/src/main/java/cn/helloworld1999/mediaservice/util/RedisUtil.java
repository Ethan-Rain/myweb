package cn.helloworld1999.mediaservice.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Getter
@Slf4j
@Component
public class RedisUtil {
    /**
     * -- GETTER --
     *  获取RedisTemplate实例
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ============================== Key 操作 ==============================

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断key:{}是否存在异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除key
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("删除key:{}异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 批量删除key
     */
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("批量删除key:{}异常:{}", keys, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                return Boolean.TRUE.equals(redisTemplate.expire(key, time, TimeUnit.SECONDS));
            }
            return false;
        } catch (Exception e) {
            log.error("设置key:{}过期时间异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取key的过期时间
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("获取key:{}过期时间异常:{}", key, e.getMessage(), e);
            return -2;
        }
    }

    /**
     * 获取key的类型
     */
    public String type(String key) {
        try {
            DataType type = redisTemplate.type(key);
            return type != null ? type.code() : "none";
        } catch (Exception e) {
            log.error("获取key:{}类型异常:{}", key, e.getMessage(), e);
            return "error";
        }
    }

    // ============================== String 操作 ==============================

    /**
     * 普通缓存获取
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置key:{}值异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置key:{}值并设置过期时间异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 递增
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("key:{}递增异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 递减
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        try {
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (Exception e) {
            log.error("key:{}递减异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    // ============================== Hash 操作 ==============================

    /**
     * HashGet
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("设置hash key:{}值异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置hash key:{}值并设置过期时间异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("设置hash key:{} field:{}值异常:{}", key, item, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建并设置时间
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置hash key:{} field:{}值并设置过期时间异常:{}", key, item, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    // ============================== Set 操作 ==============================

    /**
     * 根据key获取Set中的所有值
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("获取set key:{}值异常:{}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("判断set key:{}中是否存在value:{}异常:{}", key, value, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     */
    public long sSet(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("设置set key:{}值异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存并设置时间
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("设置set key:{}值并设置过期时间异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     */
    public long sGetSetSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取set key:{}大小异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("从set key:{}中移除值异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    // ============================== List 操作 ==============================

    /**
     * 获取list缓存的内容
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("获取list key:{}值异常:{}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     */
    public long lGetListSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取list key:{}大小异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("获取list key:{}索引:{}值异常:{}", key, index, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置list key:{}值异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存并设置时间
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置list key:{}值并设置过期时间异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置list key:{}值异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存并设置时间
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置list key:{}值并设置过期时间异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("更新list key:{}索引:{}值异常:{}", key, index, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove != null ? remove : 0;
        } catch (Exception e) {
            log.error("从list key:{}中移除值:{}异常:{}", key, value, e.getMessage(), e);
            return 0;
        }
    }

    // ============================== ZSet 操作 ==============================

    /**
     * 添加元素到ZSet
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            log.error("添加元素到zset key:{}异常:{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取ZSet中指定范围的元素
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("获取zset key:{}范围:{}~{}值异常:{}", key, start, end, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取ZSet中指定分数范围的元素
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("获取zset key:{}分数范围:{}~{}值异常:{}", key, min, max, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取ZSet中元素的分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("获取zset key:{}中元素:{}分数异常:{}", key, value, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 移除ZSet中的元素
     */
    public long zRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForZSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("从zset key:{}中移除元素异常:{}", key, e.getMessage(), e);
            return 0;
        }
    }

    // ============================== 分布式锁 ==============================

    /**
     * 获取分布式锁
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("获取分布式锁异常, lockKey:{}, requestId:{}", lockKey, requestId, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
            if (requestId.equals(currentValue)) {
                return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
            }
            return false;
        } catch (Exception e) {
            log.error("释放分布式锁异常, lockKey:{}, requestId:{}", lockKey, requestId, e);
            return false;
        }
    }

    // ============================== 批量操作 ==============================

    /**
     * 批量获取key的值
     */
    public List<Object> multiGet(Collection<String> keys) {
        try {
            return redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            log.error("批量获取key:{}值异常:{}", keys, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 批量设置key-value
     */
    public boolean multiSet(Map<String, Object> map) {
        try {
            redisTemplate.opsForValue().multiSet(map);
            return true;
        } catch (Exception e) {
            log.error("批量设置key值异常:{}", e.getMessage(), e);
            return false;
        }
    }

    // ============================== 其他操作 ==============================

    /**
     * 使用Redis的消息队列
     */
    public void convertAndSend(String channel, Object message) {
        try {
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("发送消息到channel:{}异常:{}", channel, e.getMessage(), e);
        }
    }

}