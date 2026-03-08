package cn.helloworld1999.security.controller;

import cn.helloworld1999.security.dto.*;
import cn.helloworld1999.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理登录和认证相关请求
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    // 移除了可能引起初始化问题的@Autowired注解

    /**
     * 处理登录请求
     * @param loginRequest 登录请求参数
     * @return 登录响应
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // 设置认证信息到SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            // 临时返回固定值，实际应该生成基于用户信息的token
            JwtPayloadDTO payload = new JwtPayloadDTO();
            payload.setUsername(loginRequest.getUsername());
            String token = tokenService.generateToken(payload);

            return new LoginResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 处理注册请求
     * @param registerRequest 注册请求参数
     * @return 注册响应
     */
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return null;
    }

    /**
     * 处理注销请求
     * @return 注销响应
     */
    @PostMapping("/logout")
    public String logout() {
        // 清除SecurityContext中的认证信息
        SecurityContextHolder.clearContext();
        
        // 清除JWT令牌（前端需要清除localStorage中的token）
        return "注销成功";
    }
}