package cn.helloworld1999.common.util.cryptography.strategy.impl;

import cn.helloworld1999.common.util.cryptography.strategy.CryptoStrategy;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 非对称加密策略（使用 Bouncy Castle 实现）
 * 加密使用公钥，解密使用私钥。
 */
public class RsaEncryptionStrategy implements CryptoStrategy {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 使用 RSA 公钥加密
     *
     * @param data 明文
     * @param base64PublicKey Base64 编码的公钥字符串
     * @return Base64 编码的加密结果
     */
    @Override
    public String encrypt(String data, String base64PublicKey) throws Exception {
        PublicKey publicKey = loadPublicKey(base64PublicKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用 RSA 私钥解密
     *
     * @param encryptedData Base64 编码的密文
     * @param base64PrivateKey Base64 编码的私钥字符串
     * @return 明文字符串
     */
    @Override
    public String decrypt(String encryptedData, String base64PrivateKey) throws Exception {
        PrivateKey privateKey = loadPrivateKey(base64PrivateKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 加载 Base64 编码的公钥字符串为 PublicKey 对象
     */
    private PublicKey loadPublicKey(String base64Key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 加载 Base64 编码的私钥字符串为 PrivateKey 对象
     */
    private PrivateKey loadPrivateKey(String base64Key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        return keyFactory.generatePrivate(keySpec);
    }
}
