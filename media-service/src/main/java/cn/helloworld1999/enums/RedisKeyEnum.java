package cn.helloworld1999.enums;

public enum RedisKeyEnum {
    CACHE_IMAGE("cache:images:"),
    CACHE_VIDEO("cache:videos:"),
    CACHE_BASE("cache:");

    private final String key;

    RedisKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}