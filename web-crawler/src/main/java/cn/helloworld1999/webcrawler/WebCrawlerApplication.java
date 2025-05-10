package cn.helloworld1999.webcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class WebCrawlerApplication {
    public static void main(String[] args) throws IOException, TimeoutException {

        SpringApplication.run(WebCrawlerApplication.class, args);

    }
}