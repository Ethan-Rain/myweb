package cn.helloworld1999.security.service.impl;

import cn.helloworld1999.security.config.TokenProperties;
import cn.helloworld1999.security.dto.JwtPayloadDTO;
import cn.helloworld1999.security.service.TokenService;
import cn.helloworld1999.security.strategy.TokenStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    private final Map<String, TokenStrategy> strategyMap;
    private final TokenProperties tokenProperties;

    @Autowired
    public TokenServiceImpl(Map<String, TokenStrategy> strategyMap, TokenProperties tokenProperties) {
        this.strategyMap = strategyMap;
        this.tokenProperties = tokenProperties;
    }

    private TokenStrategy getStrategy() {
        String type = tokenProperties.getStrategy();
        TokenStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported token strategy: " + type);
        }
        return strategy;
    }

    @Override
    public String generateToken(JwtPayloadDTO payload) {
        return getStrategy().generateToken(payload);
    }

    @Override
    public boolean validateToken(String token) {
        return getStrategy().validateToken(token);
    }

    @Override
    public JwtPayloadDTO extractPayload(String token) {
        return getStrategy().extractPayload(token);
    }
}
