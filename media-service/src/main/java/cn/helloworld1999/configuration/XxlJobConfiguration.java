package cn.helloworld1999.configuration;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class XxlJobConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobConfiguration.class);

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.app-name}")
    private String appName;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private Integer port;

    @Value("${xxl.job.access-token:}")
    private String accessToken;

    @Value("${xxl.job.executor.logpath:./logs/xxl-job}")
    private String logPath;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");

        // 创建日志目录
        File logPathDir = new File(logPath);
        if (!logPathDir.exists()) {
            logPathDir.mkdirs();
        }

        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        if (accessToken != null && !accessToken.isEmpty()) {
            xxlJobSpringExecutor.setAccessToken(accessToken);
        }
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(30);

        logger.info(">>>>>>>>>>> xxl-job config init completed. adminAddresses:{}, appName:{}, ip:{}, port:{}, accessToken:{}, logPath:{}",
            adminAddresses, appName, ip, port, accessToken, logPath);

        return xxlJobSpringExecutor;
    }
}