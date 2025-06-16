package cn.helloworld1999.mediaservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch; /**
 * 方法执行耗时统计切面
 */
@Slf4j
@Aspect
@Component
public class MethodExecutionTimeAspect {

    /**
     * 定义切点：拦截所有Controller层和Service层的方法
     */
    @Pointcut("(execution(* cn.helloworld1999.mediaservice.controller..*.*(..)) " +
             "|| execution(* cn.helloworld1999.mediaservice.service..*.*(..))) " +
             "&& @annotation(org.springframework.web.bind.annotation.RequestMapping)" +
             "|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
             "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
             "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
             "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
             "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void pointcut() {
        // 方法为空，仅用于定义切点
    }

    /**
     * 环绕通知：统计方法执行耗时
     */
    @Around("pointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        
        // 创建计时器
        StopWatch stopWatch = new StopWatch();
        
        try {
            // 执行目标方法
            stopWatch.start();
            Object result = joinPoint.proceed();
            stopWatch.stop();
            
            // 记录执行时间
            logMethodExecutionTime(className, methodName, stopWatch.getTotalTimeMillis(), null);
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            // 记录异常情况下的执行时间
            logMethodExecutionTime(className, methodName, stopWatch.getTotalTimeMillis(), e);
            throw e;
        }
    }

    /**
     * 记录方法执行时间
     */
    private void logMethodExecutionTime(String className, String methodName, long executionTime, Throwable e) {
        String logMessage = String.format("方法执行: %s.%s() 耗时: %d ms", 
                className, methodName, executionTime);
        
        if (e != null) {
            log.error(logMessage + " [执行异常: {}]", e.getMessage());
        } else {
            log.info(logMessage);
        }
    }
}
