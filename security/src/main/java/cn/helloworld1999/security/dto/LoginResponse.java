package cn.helloworld1999.security.dto;

/**
 * 登录响应DTO
 */
public class LoginResponse {
    private String token;
    private String message;

    public LoginResponse(String token) {
        this.token = token;
        this.message = "登录成功";
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
