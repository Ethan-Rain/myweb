package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.dto.FileData;
import cn.helloworld1999.synology.bean.FileTree;

public interface IFileService {
    FileData getFileData(String name);
    FileTree generateFileTree(FileData fileData);
}
