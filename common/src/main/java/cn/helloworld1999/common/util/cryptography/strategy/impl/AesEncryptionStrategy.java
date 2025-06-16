package cn.helloworld1999.common.util.cryptography.strategy.impl;

import cn.helloworld1999.common.util.cryptography.strategy.CryptoStrategy;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

/**
 * AES 加密策略（使用 Bouncy Castle 实现，ECB 模式 + PKCS5Padding）
 *
 * 特点：
 * - 使用 Bouncy Castle 作为加密提供者，方便与其他国密算法统一管理
 * - 采用 ECB 模式（不需要 IV），适合演示和基础用途（不推荐用于高安全要求场景）
 * - 输出为 Base64 编码字符串，便于网络传输与日志记录
 */
public class AesEncryptionStrategy implements CryptoStrategy {

    // 算法/模式/填充方式
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    // 静态代码块：项目启动时注册 Bouncy Castle 提供者
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 加密方法
     *
     * @param data 明文数据
     * @param key  密钥（建议 16 字节，对应 128 位 AES）
     * @return 加密后的 Base64 字符串
     * @throws Exception 加密过程中的任何异常
     */
    @Override
    public String encrypt(String data, String key) throws Exception {
        // 构造 AES 密钥规范
        SecretKeySpec secretKey = new SecretKeySpec(formatKey(key), ALGORITHM);

        // 获取 Cipher 实例，指定使用 BC 提供者
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 加密后进行 Base64 编码
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密方法
     *
     * @param encryptedData 加密后的 Base64 字符串
     * @param key           密钥（应与加密时保持一致）
     * @return 解密后的明文字符串
     * @throws Exception 解密过程中的任何异常
     */
    @Override
    public String decrypt(String encryptedData, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(formatKey(key), ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // 解密前先 Base64 解码
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 格式化密钥：
     * - 如果 key 长度不足 16 字节，则补 0
     * - 如果超出，则截断（仅取前 16 字节）
     *
     * @param key 用户输入的字符串密钥
     * @return 16 字节的密钥数组
     */
    private byte[] formatKey(String key) {
        byte[] keyBytes = new byte[16]; // 128-bit
        byte[] inputBytes = key.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(inputBytes, 0, keyBytes, 0, Math.min(inputBytes.length, 16));
        return keyBytes;
    }
}
