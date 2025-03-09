package cn.helloworld1999.myweb.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 切入所有Controller层方法
    @Pointcut("execution(* cn.helloworld1999.myweb.controller..*.*(..))")
    public void controllerLog() {}

    @Around("controllerLog()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestPath = attributes != null ? attributes.getRequest().getRequestURI() : "";
        
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        log.info("\n=== 请求日志 ===\n路径: {}\n方法: {}\n参数: {}\n耗时: {}ms\n响应: {}\n================",
                requestPath,
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs(),
                duration,
                result);

        return result;
    }
}
