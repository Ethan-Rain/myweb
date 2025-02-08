/*
package cn.helloworld1999.myweb.demos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

// 在Spring Boot中添加基础认证
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/source/getImageRandomTest").permitAll()  // 允许所有用户访问获取随机图片接口
                .antMatchers("/api/source/importImages").hasRole("USER")  // 允许具有USER角色的用户访问导入图片接口
                .anyRequest().authenticated()  // 其他所有请求需认证
                .and()
            .httpBasic();  // 启用HTTP Basic认证
    }

  // SecurityConfig.java 需要显式配置密码编码器
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance()) // 添加编码器
        .withUser("haha").password("haha").roles("USER"); // 移除{noop}前缀
}

}*/
