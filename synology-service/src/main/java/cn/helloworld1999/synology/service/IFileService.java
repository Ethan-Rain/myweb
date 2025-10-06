package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.bean.FileData;
import cn.helloworld1999.synology.bean.FileTree;
import cn.helloworld1999.synology.service.impl.FileService;

import java.io.File;
import java.util.Map;

public interface IFileService {
    FileData getFileData(String name);
    FileTree generateFileTree(FileData fileData);
}
