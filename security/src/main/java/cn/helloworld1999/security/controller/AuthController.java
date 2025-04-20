package cn.helloworld1999.security.controller;

import cn.helloworld1999.domain.Users;
import cn.helloworld1999.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证控制器
 * 处理登录、注册等认证相关的请求
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        // 如果用户已登录，则重定向到首页
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/home";
        }
        return "login";  // 返回login.html模板
    }

    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        // 如果用户已登录，则重定向到首页
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/home";
        }
        return "register";  // 返回register.html模板
    }

    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public String registerUser(Users user, Model model) {
        // 验证用户输入
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "用户名、密码和邮箱不能为空");
            return "register";
        }

        // 注册用户
        boolean success = userService.registerUser(user);
        if (success) {
            return "redirect:/login?registered";  // 注册成功，重定向到登录页
        } else {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/user/info")
    public String userInfo(Model model) {
        // 获取当前认证的用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // 查询用户信息
        Users user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        
        return "user-info";  // 返回user-info.html模板
    }
}
