package cn.helloworld1999.synology.api;

import cn.helloworld1999.synology.annotation.AutoLogin;
import cn.helloworld1999.synology.client.SynologyFeignClient;
import cn.helloworld1999.synology.config.SynologyApiProperties;
import groovy.util.logging.Log4j2;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Log4j2
@Service
@Data
@RequiredArgsConstructor
public class SynologyAuthService extends SynologyBaseService{
    private Map<String, Object> params = new HashMap<>();
    private final SynologyFeignClient feignClient;
    private final SynologyApiProperties properties;

    public Map<String, Object> login(String account, String passwd) {
        // 每次登录前清空参数，避免残留旧值
        params.clear();
        if (account == null || passwd == null) {
            params.put("account", properties.getAccount());
            params.put("passwd", properties.getPasswd());
        } else {
            params.put("account", account);
            params.put("passwd", passwd);
        }
        params.put("api", "SYNO.API.Auth");
        params.put("version", "6");
        params.put("method", "login");
        params.put("session", "FileStation");
        params.put("format", "sid");

        // 登录成功后，从响应中提取 sid 并赋值给实例变量

        Map<String, Object> loginResult = feignClient.login(params);
        log.info("登录結果：{}",loginResult);
        if ((Boolean) loginResult.get("success")) {
            Map<String, Object> data = (Map<String, Object>) loginResult.get("data");
            super.setSid((String) data.get("sid")); // 保存 sid 到实例变量
        }
        return loginResult;
    }
    public Map<String, Object> sysLogin() {
        // 每次登录前清空参数，避免残留旧值
        params.clear();
        params.put("account", properties.getAccount());
        params.put("passwd", properties.getPasswd());
        params.put("api", "SYNO.API.Auth");
        params.put("version", "6");
        params.put("method", "login");
        params.put("session", "FileStation");
        params.put("format", "sid");

        // 登录成功后，从响应中提取 sid 并赋值给实例变量
        Map<String, Object> loginResult = feignClient.login(params);
        if ((Boolean) loginResult.get("success")) {
            Map<String, Object> data = (Map<String, Object>) loginResult.get("data");
            super.setSid((String) data.get("sid")); // 保存 sid 到实例变量
        }
        return loginResult;
    }

    @AutoLogin
    public void aspectTest() {
        System.out.println("aspect test");
        System.out.println(this.params); // 此时 params 应为实例变量
    }
}