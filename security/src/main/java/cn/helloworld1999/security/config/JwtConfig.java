package cn.helloworld1999.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * JWT配置类
 * 配置JWT相关的bean
 */
@Configuration
public class JwtConfig {

    @Autowired
    private UserDetailsService userDetailsService;


}
