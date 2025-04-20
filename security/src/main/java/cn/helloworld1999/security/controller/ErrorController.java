package cn.helloworld1999.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 错误控制器
 * 处理各种错误页面
 */
@Controller
public class ErrorController {

    /**
     * 访问拒绝页面
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";  // 返回error/access-denied.html模板
    }
}
