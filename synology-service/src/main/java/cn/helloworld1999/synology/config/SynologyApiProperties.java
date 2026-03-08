package cn.helloworld1999.synology.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "synology.api")
public class SynologyApiProperties {
    private String baseUrl;
    private String account;
    private String passwd;
    private String tokenTimeout;
}
