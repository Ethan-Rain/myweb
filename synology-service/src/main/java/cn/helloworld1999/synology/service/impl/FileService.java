package cn.helloworld1999.synology.service.impl;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.service.IFileService;
import groovy.util.logging.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.json.JSONUtil;
import java.util.List;
import java.util.Map;

@Slf4j
@Log4j
@Service
public class FileService implements IFileService {
    @Autowired
    SynologyReadService synologyReadService;
    public class FilePathTrue{
        String isDir;
        String name;
        String path;
        List<FilePathTrue> children;
    }
    @Override
    public Map<String, Object> getFileTree(String name) {
       String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList(name));
        log.info("resultJson:{}",resultJson);
        return Map.of();
    }
}
