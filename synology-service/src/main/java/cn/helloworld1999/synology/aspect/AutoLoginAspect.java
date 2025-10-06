package cn.helloworld1999.synology.aspect;

import cn.helloworld1999.synology.api.SynologyAuthService;
import cn.helloworld1999.synology.api.SynologyBaseService;
import cn.helloworld1999.synology.config.SynologyApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect // 声明这是一个AOP切面
@Component // 交给Spring管理
@Slf4j
public class AutoLoginAspect {
    public static String sid = null;
    public static long lastLoginTime = 0L;
    @Autowired
    SynologyAuthService synologyAuthService;
    @Autowired
    ObjectMapper objectMapper;
    @Before("@annotation(cn.helloworld1999.synology.annotation.AutoLogin)")
    public void beforeAutoLogin(JoinPoint joinPoint) {
        SynologyApiProperties properties = new SynologyApiProperties();
        Object target = joinPoint.getTarget();
        if (!(target instanceof SynologyBaseService service)) {
            log.info("不是或没有继承SynologyBaseService类,不进行自动登录");
            return;
        }
        log.info("进入自动登录切面");
        if (service.getSid() != null) {
            log.info("已存在sid:{}", sid);
            if (System.currentTimeMillis() - lastLoginTime > Long.parseLong(properties.getTokenTimeout())) {
                log.info("sid已过期,重新登录");
                service.setSidAndCookie(sid);
                log.info("已为目标对象赋值sid:{}", service.getSid());
                log.info("已为目标对象赋值cookie:{}", service.getCookie());
            }
            return;
        }
        try {
            JsonNode jsonNode = objectMapper.valueToTree(synologyAuthService.sysLogin());
            sid = (jsonNode.get("data").get("sid").toString());
            lastLoginTime = System.currentTimeMillis();
            // target的类型
            log.info("目标对象类型：{}", target.getClass().getName());
            // 3. 调用setter方法赋值
            service.setSidAndCookie(sid);
            log.info("已为目标对象赋值sid:{}", service.getSid());
            log.info("已为目标对象赋值cookie:{}", service.getCookie());
        } catch (Exception e) {
           log.error(e.getMessage());
        }
    }
}
