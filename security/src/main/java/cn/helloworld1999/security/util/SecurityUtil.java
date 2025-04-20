package cn.helloworld1999.security.util;

import cn.helloworld1999.domain.Users;
import cn.helloworld1999.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 安全工具类
 * 提供获取当前登录用户信息等工具方法
 */
@Component
public class SecurityUtil {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户的用户名
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * 获取当前登录用户的User对象
     */
    public Users getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        return userService.getUserByUsername(username);
    }

    /**
     * 获取当前登录用户的角色列表
     */
    public Collection<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * 检查当前用户是否拥有指定角色
     */
    public boolean hasRole(String role) {
        Collection<String> roles = getCurrentUserRoles();
        if (roles == null) {
            return false;
        }
        
        // 如果角色不以"ROLE_"开头，则添加前缀
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        
        return roles.contains(role);
    }
}
