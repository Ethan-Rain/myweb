package cn.helloworld1999.synology.service;

import java.util.Map;

public interface IFileService {
    Map<String, Object> getFileTree(String name);
}
