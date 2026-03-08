package cn.helloworld1999.synology;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.helloworld1999.synology.client")
@SpringBootApplication
@MapperScan("cn.helloworld1999.synology.mapper") // 确保 Mapper 包路径正确
public class SynologyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SynologyServiceApplication.class, args);
    }
}
