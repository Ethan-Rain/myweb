package cn.helloworld1999.security.controller;

import cn.helloworld1999.domain.Users;
import cn.helloworld1999.security.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 首页
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // 获取当前登录用户信息
        Users currentUser = securityUtil.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
        }
        
        return "home";  // 返回home.html模板
    }
}
