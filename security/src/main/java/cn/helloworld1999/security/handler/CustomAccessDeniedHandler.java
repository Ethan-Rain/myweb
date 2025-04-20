package cn.helloworld1999.security.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义访问拒绝处理器
 * 用于处理权限不足的情况
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 判断是否是Ajax请求
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            // Ajax请求，返回JSON格式的错误信息
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"权限不足，无法访问\",\"status\":403}");
        } else {
            // 普通请求，重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/access-denied");
        }
    }
}
