package cn.helloworld1999.common.dto;

public class CryptoRequest {
    private String data; // 需要加密的数据
    private String key;  // 共享的加密密钥

    // Getters 和 Setters
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
