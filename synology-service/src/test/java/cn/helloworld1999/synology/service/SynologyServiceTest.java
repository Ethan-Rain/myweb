package cn.helloworld1999.synology.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SynologyServiceTest {
    @Autowired
    SynologyService synologyService;

    @Test
    void aspectTest() {
        synologyService.aspectTest();
    }

    @Test
    void getFileList() {
        System.out.println(synologyService.getFileList("/存储空间"));
        System.out.println("哦哦哦");
    }
}
