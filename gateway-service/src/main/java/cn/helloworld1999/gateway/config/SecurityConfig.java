package cn.helloworld1999.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 禁用 CSRF
            .authorizeRequests()
                .antMatchers("/login").permitAll() // 允许所有用户访问登录接口
                .anyRequest().authenticated() // 其他请求需要认证
            .and()
            .formLogin()
                .loginProcessingUrl("/login") // 登录处理 URL
                .permitAll(); // 允许所有用户访问登录页面
    }
}