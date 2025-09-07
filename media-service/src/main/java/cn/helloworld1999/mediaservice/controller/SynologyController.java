package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.service.SynologyFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/synology")
public class SynologyController {

    @Autowired
    private SynologyFileService synologyFileService;

    /**
     * 测试连接并列出指定目录（默认列出根目录）
     */
    @GetMapping("/test")
    public ResponseEntity<Object> test(@RequestParam(required = false, defaultValue = "/") String path) {
        try {
            List<Map<String, Object>> files = synologyFileService.listFiles(path, false);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Synology test failed", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}

