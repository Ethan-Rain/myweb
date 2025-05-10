package cn.helloworld1999.userservice.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("media-service")
public interface Test {
    @RequestMapping(method = RequestMethod.GET,
            value = "/fegintest")
    String feginTest();
}
