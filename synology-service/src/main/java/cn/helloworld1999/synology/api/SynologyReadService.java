package cn.helloworld1999.synology.api;

import cn.helloworld1999.synology.annotation.AutoLogin;
import cn.helloworld1999.synology.client.SynologyFeignClient;
import cn.helloworld1999.synology.config.SynologyApiProperties;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
@RequiredArgsConstructor
public class SynologyReadService extends SynologyBaseService{
    private Map<String, Object> params = new HashMap<>();
    private final SynologyFeignClient feignClient;
    private final SynologyApiProperties properties;
    private String sid;
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
        List<String> additionalFields = List.of(
                "real_path",
                "size",
                "owner",
                "time",
                "perm",
                "type",
                "mount_point_type",
                "description",
                "indexed"
        );

        fileListParams.put("additional", JSONUtil.toJsonStr(additionalFields));

        fileListParams.put("offset", 0);
        fileListParams.put("limit", 1000);
        fileListParams.put("sort_by", "name");
        fileListParams.put("sort_direction", "ASC");  // 注意大小写
        fileListParams.put("action", "list");         // 新增
        fileListParams.put("check_dir", true);        // 新增
        fileListParams.put("filetype", "all");



        // 调用 Feign 客户端（使用局部变量 fileListParams）
        System.out.println("cookie: " + getCookie());
        System.out.println("请求参数: " + fileListParams);
        return feignClient.getFileList(fileListParams,getCookie());
    }
}
