package cn.helloworld1999.myweb.exception;

import lombok.Getter;

/**
 * 业务逻辑异常，用于表示可预期的业务逻辑错误
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final int code;
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message) {
        this(400, message);
    }
    
    public static BusinessException notFound(String resource) {
        return new BusinessException(404, resource + " 不存在");
    }
    
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }
    
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }
    
    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message);
    }
}
