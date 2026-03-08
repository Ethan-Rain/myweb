package cn.helloworld1999.synology.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SynologyReadServiceTest {

    @Autowired
    private SynologyReadService synologyReadService;

    @Test
    void getFileList() {
        System.out.println(synologyReadService.getFileList("/存储空间"));
        System.out.println("哦哦哦");
    }
}