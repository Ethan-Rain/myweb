package cn.helloworld1999.common.util.cryptography;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

public class RsaKeyGenerator {

    public static Map<String, Object> generateRsaKey(int keySize) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        generator.initialize(Math.max(keySize, 2048)); // 2048位更安全
        KeyPair keyPair = generator.generateKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        System.out.println("🔑 公钥：\n" + publicKey);
        System.out.println("🔐 私钥：\n" + privateKey);

        return Map.of("publicKey", publicKey, "privateKey", privateKey);
    }
}
