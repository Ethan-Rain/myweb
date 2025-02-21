package cn.helloworld1999.myweb.dto;

import lombok.Data;

@Data
public class ResponseResult<T> {
    private int code;
    private String message;
    private T data;

    // 私有构造函数，防止外部直接实例化
    private ResponseResult() {}

    // 成功返回结果
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(200);  // 假设200代表成功
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    // 失败返回结果
    public static <T> ResponseResult<T> error(int code, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // Getters and Setters...
}