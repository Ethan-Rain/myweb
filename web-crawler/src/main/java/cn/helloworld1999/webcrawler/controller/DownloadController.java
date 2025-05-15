package cn.helloworld1999.webcrawler.controller;

import cn.helloworld1999.webcrawler.strategy.DownloaderStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/downloads")
public class DownloadController {
    @Autowired
    DownloaderStrategy downloaderStrategy; //DownloaderStrategy
    @PostMapping
    public ResponseEntity<String> initiateDownload(@RequestBody JsonNode message) throws IOException {
         if (downloaderStrategy.start(message)) {
             return ResponseEntity.ok("下载请求已处理成功");
        }
        return ResponseEntity.ok("下载请求处理失败");
    }
}
