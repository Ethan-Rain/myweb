package cn.helloworld1999.synology.aspect;

import cn.helloworld1999.synology.annotation.AutoLogin;
import cn.helloworld1999.synology.service.SynologyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect // 声明这是一个AOP切面
@Component // 交给Spring管理
public class AutoLoginAspect {
    public static String sid = null;
    @Autowired
    SynologyService synologyService;
    @Autowired
    ObjectMapper objectMapper;
    @Before("@annotation(cn.helloworld1999.synology.annotation.AutoLogin)")
    public void beforeAutoLogin(JoinPoint joinPoint) {
        try {
            JsonNode jsonNode = objectMapper.valueToTree(synologyService.login(null, null));
            sid = (jsonNode.get("data").get("sid").toString());
            Object target = joinPoint.getTarget();

            // 3. 判断目标对象类型，调用setter方法赋值
            if (target instanceof SynologyService service) {
                service.getParams().put("sid",sid); // 给目标对象的sid属性赋值
                System.out.println("已为目标对象赋值sid：" + sid);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
