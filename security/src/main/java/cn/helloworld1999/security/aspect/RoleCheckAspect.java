package cn.helloworld1999.security.aspect;

import cn.helloworld1999.security.annotation.RequiresRole;
import cn.helloworld1999.security.util.SecurityUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色检查切面
 * 实现自定义角色权限注解的切面处理
 */
@Aspect
@Component
public class RoleCheckAspect {

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 环绕通知，在方法执行前后进行处理
     */
    @Around("@annotation(cn.helloworld1999.security.annotation.RequiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        RequiresRole requiresRole = method.getAnnotation(RequiresRole.class);
        
        // 获取需要的角色列表
        String[] roles = requiresRole.value().split(",");
        String logical = requiresRole.logical().toUpperCase();
        
        // 检查当前用户是否具有所需角色
        boolean hasRole = false;
        if ("OR".equals(logical)) {
            // 具有任一角色即可
            hasRole = Arrays.stream(roles)
                    .anyMatch(role -> securityUtil.hasRole(role.trim()));
        } else {
            // 默认为AND，需要同时具有所有角色
            hasRole = Arrays.stream(roles)
                    .allMatch(role -> securityUtil.hasRole(role.trim()));
        }
        
        // 如果没有所需角色，抛出访问拒绝异常
        if (!hasRole) {
            throw new AccessDeniedException("权限不足，需要角色：" + requiresRole.value());
        }
        
        // 有所需角色，继续执行原方法
        return joinPoint.proceed();
    }
}
