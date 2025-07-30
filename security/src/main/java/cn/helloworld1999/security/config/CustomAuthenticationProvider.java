package cn.helloworld1999.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * 自定义认证提供者
 * 用于处理用户认证逻辑
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取用户名和密码
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // 加载用户信息
            org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) userDetailsService.loadUserByUsername(username);

            // 验证密码 (临时修改为明文比较)
            if (!password.equals(userDetails.getPassword())) {
                throw new BadCredentialsException("密码错误");
            }

            // 返回认证成功的令牌
            return new UsernamePasswordAuthenticationToken(
                userDetails, 
                userDetails.getPassword(),
                userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("用户不存在");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
