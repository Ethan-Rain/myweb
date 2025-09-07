package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.annotation.AutoLogin;
import cn.helloworld1999.synology.client.SynologyFeignClient;
import cn.helloworld1999.synology.config.SynologyApiProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Data
@RequiredArgsConstructor
public class SynologyService {
    // 1. 移除 static，改为实例变量（每个实例独立，避免线程安全问题）
    private Map<String, Object> params = new HashMap<>();
    private final SynologyFeignClient feignClient;
    private final SynologyApiProperties properties;
    // 2. sid 改为实例变量（AOP 切面应操作实例变量）
    private String sid;

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
        if ((Boolean) loginResult.get("success")) {
            Map<String, Object> data = (Map<String, Object>) loginResult.get("data");
            this.sid = (String) data.get("sid"); // 保存 sid 到实例变量
        }
        return loginResult;
    }

    @AutoLogin
    public void aspectTest() {
        System.out.println("aspect test");
        System.out.println(this.params); // 此时 params 应为实例变量
    }

    @AutoLogin
    public Map<String, Object> getFileList(String folderPath) {
        // 3. 正确使用局部变量（或实例变量），避免与类变量冲突
        Map<String, Object> fileListParams = new HashMap<>();

        // 必选参数（修复：用 sid 而非 _sid）
        fileListParams.put("api", "SYNO.FileStation.List");
        fileListParams.put("version", "2"); // 改用稳定版本 2（3 可能兼容问题）
        fileListParams.put("method", "list");
        fileListParams.put("folder_path", folderPath);
        fileListParams.put("sid", this.sid); // 使用实例变量 sid（AOP 应赋值给它）

        // 可选参数（修复：time 改为 mtime，群晖 API 无 time 字段）
        fileListParams.put("additional", "real_path,size,owner,mtime,perm");

        // 分页和排序参数
        fileListParams.put("limit", 50);
        fileListParams.put("sort_by", "name");
        fileListParams.put("sort_direction", "asc");

        // 调用 Feign 客户端（使用局部变量 fileListParams）
        String cookie = "id=" + this.sid;
        System.out.println("请求参数: " + fileListParams);
        return feignClient.getFileList(fileListParams,cookie);
    }
}