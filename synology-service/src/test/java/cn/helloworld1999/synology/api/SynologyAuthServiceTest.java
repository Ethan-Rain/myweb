package cn.helloworld1999.synology.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SynologyAuthServiceTest {
    @Autowired
    SynologyAuthService synologyAuthService;

    @Test
    void aspectTest() {
        synologyAuthService.aspectTest();
    }


}
