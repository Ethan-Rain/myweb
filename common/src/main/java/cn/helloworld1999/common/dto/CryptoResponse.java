package cn.helloworld1999.common.dto;

public class CryptoResponse {
    private String result; // 加密或解密后的结果
    private String status; // 状态信息

    // Getters 和 Setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
