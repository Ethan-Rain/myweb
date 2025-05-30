package cn.helloworld1999.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Hello {
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello," + name;
    }
}
