package cn.helloworld1999.mediaservice.service;

import java.util.List;
import java.util.Map;

public interface SynologyFileService {
    /**
     * 获取指定路径下的所有文件信息
     * @param path 文件路径
     * @param recursive 是否递归获取子目录
     * @return 文件信息列表
     */
    List<Map<String, Object>> listFiles(String path, boolean recursive);

    /**
     * 获取文件详细信息
     * @param path 文件路径
     * @return 文件详细信息
     */
    Map<String, Object> getFileInfo(String path);
}
