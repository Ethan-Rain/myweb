package cn.helloworld1999.common.util.strategy;

public interface CryptoStrategy {
    String encrypt(String data, String key) throws Exception;
    String decrypt(String encryptedData, String key) throws Exception;
}
