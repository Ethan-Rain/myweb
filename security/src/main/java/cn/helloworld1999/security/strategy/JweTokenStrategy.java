package cn.helloworld1999.security.strategy;

import cn.helloworld1999.security.dto.JwtPayloadDTO;
import org.springframework.stereotype.Component;

@Component("JWE")
public class JweTokenStrategy implements TokenStrategy{
    @Override
    public String generateToken(JwtPayloadDTO payload) {
        return "";
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public JwtPayloadDTO extractPayload(String token) {
        return null;
    }

    @Override
    public String getType() {
        return "";
    }
}
