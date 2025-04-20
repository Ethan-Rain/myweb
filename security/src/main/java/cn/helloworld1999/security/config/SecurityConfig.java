package cn.helloworld1999.security.config;

import cn.helloworld1999.security.handler.CustomAccessDeniedHandler;
import cn.helloworld1999.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 启用方法级别的权限控制
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * 密码编码器
     * 用于对密码进行加密和验证
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * 用于处理认证请求
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /**
     * 自定义访问拒绝处理器
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /**
     * 安全过滤器链
     * 配置Spring Security的各种规则
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 配置请求授权规则
            .authorizeRequests()
                // 允许所有人访问静态资源
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // 允许所有人访问登录页和注册页
                .antMatchers("/login", "/register", "/access-denied").permitAll()
                // 所有其他请求需要认证
                .anyRequest().authenticated()
                .and()
            // 配置表单登录
            .formLogin()
                .loginPage("/login")           // 自定义登录页URL
                .defaultSuccessUrl("/home")    // 登录成功后的跳转URL
                .failureUrl("/login?error")    // 登录失败后的跳转URL
                .permitAll()
                .and()
            // 配置登出
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
            // 配置访问拒绝处理器
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
            // 禁用CSRF保护（在开发阶段可以禁用，生产环境建议启用）
            .csrf().disable();
                
        return http.build();
    }
}
