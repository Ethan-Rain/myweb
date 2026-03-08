package cn.helloworld1999.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenProperties {
    @Value("${security.token.strategy:JWT}")
    private String strategy;

    public String getStrategy() {
        return strategy;
    }
}
