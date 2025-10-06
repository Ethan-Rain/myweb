package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.api.SynologyFileListTest;
import cn.helloworld1999.synology.api.SynologyReadService;
import cn.hutool.json.JSONUtil;
import groovy.util.logging.Log4j;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Slf4j
@Service
@SpringBootTest
public class FileService implements IFileService {
    @Autowired
    SynologyReadService synologyReadService;

    @Override
    public Map<String, Object> getFileTree(String name) {
        return Map.of();
    }
    @Data
    static public class FilePathTrue{
        String isDir;
        String name;
        String path;
        List<FilePathTrue> children;
    }
    @Test
    public void getFileTree() {
        //SynologyFileListTest test = new SynologyFileListTest();
        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList("/存储空间"));
        log.info("resultJson:{}",resultJson);
    }
}
