package cn.helloworld1999.mediaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "synology")
public class SynologyConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String sessionName = "FileStation";
    private String apiVersion = "2";
    // 是否使用 HTTPS（如果为 true，常用端口为 5001）
    private boolean useHttps = false;
    // 是否在 HTTPS 下跳过证书验证（仅用于测试环境）
    private boolean skipSsl = false;
}
