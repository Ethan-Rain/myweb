package cn.helloworld1999.synology.service.impl;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.dto.FileData;
import cn.helloworld1999.synology.dto.FileItem;
import cn.helloworld1999.synology.dto.FileListResult;
import cn.helloworld1999.synology.bean.FileTree;
import cn.helloworld1999.synology.entity.FileInfoEntity;
import cn.helloworld1999.synology.mapper.FileInfoMapper;
import cn.helloworld1999.synology.service.IFileService;
import groovy.util.logging.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.json.JSONUtil;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Log4j
@Service
public class FileService implements IFileService {
    @Autowired
    SynologyReadService synologyReadService;
    @Autowired
    FileInfoMapper fileInfoMapper;

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

    public void insertBatchSafe(List<FileInfoEntity> list) {
        int batchSize = 100; // 每批100条
        for (int i = 0; i < list.size(); i += batchSize) {
            List<FileInfoEntity> batch = list.subList(i, Math.min(i + batchSize, list.size()));
            fileInfoMapper.insertBatch(batch);
        }
    }

    public FileTree getFileTreeByFilePath(String path, boolean needInsertDB, boolean needInsertRedis) {
        Time startTime = new Time(System.currentTimeMillis());
        FileData fileData = null;
        FileTree fileTree = new FileTree();
        if (path == null) path = "/存储空间/2.1.newSeSe";

        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList(path));
        System.out.println("返回的json字符串：");
        System.out.println(resultJson);
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);
        if (fileListResult.isSuccess()) {
            fileData = fileListResult.getData();
            System.out.println(fileData);

            List<FileItem> files = fileData.getFiles();
            System.out.println("测试输出：");
            System.out.println(files.size());
            System.out.println(files);

            fileTree.setName("根目录:" + path);
            fileTree.setPath(path);
            fileTree.setIsdir(true);
            fileTree.setChildrenCount(files.size());
            fileTree.setChildren(files.stream().map(fileItem -> {
                FileTree child = new FileTree();
                child.fileItemToFileTree(fileItem);
                return child;
            }).toList());
            List<FileTree> flatList = new ArrayList<>();
            FileTree.getAllFileTree(fileTree, synologyReadService, flatList);
            Time endTime = new Time(System.currentTimeMillis());
            System.out.println("查询耗时：" + (endTime.getTime() - startTime.getTime()));
            if (needInsertRedis){
                // TODO
            }
            if (needInsertDB) {
                List<FileInfoEntity> entityList = flatList.stream()
                        .map(FileInfoEntity::convertToEntity)
                        .toList();
                long startInsert = System.currentTimeMillis();
                insertBatchSafe(entityList);
                long endInsert = System.currentTimeMillis();
                System.out.println("插入数据库耗时：" + (endInsert - startInsert) + "ms");
            }
        }
        return fileTree;
    }
}