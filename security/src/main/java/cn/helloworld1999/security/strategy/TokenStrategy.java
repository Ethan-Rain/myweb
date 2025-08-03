package cn.helloworld1999.security.strategy;

import cn.helloworld1999.security.dto.JwtPayloadDTO;

public interface TokenStrategy {

    /**
     * 生成 JWT 或 JWE token
     */
    String generateToken(JwtPayloadDTO payload);

    /**
     * 验证 token 是否合法（签名/加密是否正确、是否过期等）
     */
    boolean validateToken(String token);

    /**
     * 提取 token 中的载荷信息（可能需要先解密再解码）
     */
    JwtPayloadDTO extractPayload(String token);

    /**
     * 获取当前策略类型，例如 JWT / JWE
     */
    String getType(); // e.g., "JWT", "JWE"
}
