package cn.helloworld1999.synology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "cn.helloworld1999.synology.client")
public class SynologyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SynologyServiceApplication.class, args);
    }
}
