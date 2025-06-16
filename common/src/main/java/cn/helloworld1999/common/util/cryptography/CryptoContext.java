package cn.helloworld1999.common.util.cryptography;

import cn.helloworld1999.common.util.cryptography.strategy.CryptoStrategy;
import cn.helloworld1999.common.util.cryptography.strategy.impl.AesEncryptionStrategy;
import cn.helloworld1999.common.util.cryptography.strategy.impl.RsaEncryptionStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略上下文类：根据策略类型统一调度不同的加解密实现
 */
public class CryptoContext {

    // 策略注册表
    private static final Map<CryptoType, CryptoStrategy> STRATEGY_MAP = new HashMap<>();

    // 注册静态策略
    static {
        STRATEGY_MAP.put(CryptoType.AES, new AesEncryptionStrategy());
        STRATEGY_MAP.put(CryptoType.RSA, new RsaEncryptionStrategy());
        // 后续可以注册更多策略
    }

    /**
     * 执行加密
     *
     * @param data 明文数据
     * @param key 密钥（或公钥）
     * @param type 策略类型
     * @return 密文
     */
    public static String encrypt(String data, String key, CryptoType type) throws Exception {
        CryptoStrategy strategy = STRATEGY_MAP.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到加密策略: " + type);
        }
        return strategy.encrypt(data, key);
    }

    /**
     * 执行解密
     *
     * @param encryptedData 密文数据
     * @param key 密钥（或私钥）
     * @param type 策略类型
     * @return 明文
     */
    public static String decrypt(String encryptedData, String key, CryptoType type) throws Exception {
        CryptoStrategy strategy = STRATEGY_MAP.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到解密策略: " + type);
        }
        return strategy.decrypt(encryptedData, key);
    }

    /**
     * 可选：支持动态注册（扩展用）
     */
    public static void registerStrategy(CryptoType type, CryptoStrategy strategy) {
        STRATEGY_MAP.put(type, strategy);
    }
}
