package cn.helloworld1999.common.util;

import cn.helloworld1999.common.util.strategy.CryptoStrategy;

public class CryptoContext {
    private CryptoStrategy strategy;

    public void setStrategy(CryptoStrategy strategy) {
        this.strategy = strategy;
    }

    public String encrypt(String data, String key) throws Exception {
        return strategy.encrypt(data, key);
    }

    public String decrypt(String encryptedData, String key) throws Exception {
        return strategy.decrypt(encryptedData, key);
    }
}
