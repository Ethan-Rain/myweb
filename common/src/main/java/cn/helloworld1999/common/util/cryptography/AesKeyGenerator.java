package cn.helloworld1999.common.util.cryptography;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

public class AesKeyGenerator {
    public static String generateAesKey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
        generator.initialize(Math.max(keySize, 2048));
        KeyPair keyPair = generator.generateKeyPair();
        String key = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        System.out.println("🔑 密钥：\n" + key);
        return key;
    }
}
