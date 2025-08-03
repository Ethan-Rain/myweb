package cn.helloworld1999.security.dto;

import lombok.Data;

import java.util.List;

/**
 * JWT载荷数据传输对象
 * 用于表示JWT令牌中的声明(claims)信息
 * 包含标准声明和自定义声明字段
 */
@Data
public class JwtPayloadDTO {
    /**
     * 主题声明(sub claim)
     * 通常是用户标识符
     */
    private String sub;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色列表
     */
    private List<String> roles;
    
    /**
     * 签发时间(iat claim)
     * 以秒为单位的时间戳
     */
    private Long iat;
    
    /**
     * 过期时间(exp claim)
     * 以秒为单位的时间戳
     */
    private Long exp;
    
    /**
     * 客户端ID
     */
    private String clientId;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 令牌类型
     */
    private TokenType tokenType;
    /**
     * 安全级别
     * 如 NORMAL, ZERO_TRUST
     */
    private String securityLevel;

    /**
     * 令牌类型枚举
     * ACCESS: 访问令牌
     * REFRESH: 刷新令牌
     */
    public enum TokenType {
        ACCESS, REFRESH
    }
}