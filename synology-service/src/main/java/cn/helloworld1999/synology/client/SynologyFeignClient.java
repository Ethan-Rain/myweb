package cn.helloworld1999.synology.client;

import cn.helloworld1999.synology.config.FeignHttpsConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
@Service
@FeignClient(
        name = "synologyApi",
        url = "${synology.api.base-url}",
        configuration = FeignHttpsConfig.class
)
public interface SynologyFeignClient {

    @GetMapping("/auth.cgi")
    Map<String, Object> login(@RequestParam Map<String, Object> params);

    @GetMapping("/entry.cgi")
    Map<String, Object> callApi(@RequestParam Map<String, Object> params);

    // 对应的FeignClient接口方法（供参考）
    @GetMapping("/entry.cgi")
    Map<String, Object> getFileList(
            @RequestParam Map<String, Object> params,
            @RequestHeader(value = "Cookie", required = false) String cookie
    );
}
