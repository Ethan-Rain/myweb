package cn.helloworld1999.security.service;

import cn.helloworld1999.security.dto.JwtPayloadDTO;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    String generateToken(JwtPayloadDTO payload);
    boolean validateToken(String token);
    JwtPayloadDTO extractPayload(String token);
}
