package cn.helloworld1999.security.controller;

import cn.helloworld1999.common.domain.Users;
import cn.helloworld1999.security.dto.LoginRequest;
import cn.helloworld1999.security.dto.LoginResponse;
import cn.helloworld1999.security.dto.RegisterRequest;
import cn.helloworld1999.security.dto.RegisterResponse;
import cn.helloworld1999.security.service.RegisterService;
import cn.helloworld1999.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RegisterService registerService;

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
            String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

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
        try {
            Users user = registerService.register(registerRequest);
            return new RegisterResponse(user.getId(), "注册成功");
        } catch (Exception e) {
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
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