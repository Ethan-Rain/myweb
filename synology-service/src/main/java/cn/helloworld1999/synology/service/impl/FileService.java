package cn.helloworld1999.synology.service.impl;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.dto.FileData;
import cn.helloworld1999.synology.dto.FileListResult;
import cn.helloworld1999.synology.bean.FileTree;
import cn.helloworld1999.synology.service.IFileService;
import groovy.util.logging.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.json.JSONUtil;

@Slf4j
@Log4j
@Service
public class FileService implements IFileService {
@Autowired
SynologyReadService synologyReadService;
    @Override
    public FileData getFileData(String name) {
        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList(name));
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);
        if (fileListResult.isSuccess()) {
            FileData fileData = fileListResult.getData();
            return fileListResult.getData();
        }
        return null;
    }

    @Override
    public FileTree generateFileTree(FileData fileData) {
        return null;
    }
}