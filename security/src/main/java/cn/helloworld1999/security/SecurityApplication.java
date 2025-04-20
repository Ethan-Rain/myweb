package cn.helloworld1999.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("cn.helloworld1999.mapper")
@ComponentScan(basePackages = {"cn.helloworld1999.security", "cn.helloworld1999.mapper", "cn.helloworld1999.domain"})
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

}
