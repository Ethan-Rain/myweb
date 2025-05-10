package cn.helloworld1999.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hi {
    @Autowired
    private Test test;
    @Autowired
    Environment environment;
    @GetMapping("/hi")
    public String hi() {
        return "hi"+environment.getProperty("server.port");
    }
    @GetMapping("/fegintest")
    public String getFeginTestByFeign() {
        System.out.println("fegintest");
        return test.feginTest();
    }
}
