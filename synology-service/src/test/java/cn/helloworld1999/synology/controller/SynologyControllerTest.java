package cn.helloworld1999.synology.controller;

import cn.helloworld1999.synology.service.SynologyService;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest // 让 Spring Boot 启动上下文
class SynologyControllerTest {

    @Autowired
    private SynologyService synologyService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginDefault() {
        try {
            JsonNode jsonNode = objectMapper.valueToTree(synologyService.login(null, null));
            System.out.println(jsonNode.get("data").get("sid").toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
