package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.dto.FileData;
import cn.helloworld1999.synology.dto.FileItem;
import cn.helloworld1999.synology.dto.FileListResult;
import cn.helloworld1999.synology.bean.FileTree;
import cn.helloworld1999.synology.entity.FileInfoEntity;
import cn.helloworld1999.synology.mapper.FileInfoMapper;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@SpringBootTest
public class FileServiceTest implements IFileService {
    @Autowired
    public SynologyReadService synologyReadService;
    @Autowired
    public FileInfoMapper fileInfoMapper;

    @Test
    public void getFileData() {
        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList("/存储空间"));
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);
        if (fileListResult.isSuccess()) {
            FileData fileData = fileListResult.getData();
            System.out.println(fileData);
        }
    }

    FileTree fileTree = new FileTree();
    @Test
    public void getFileTree(){
        Time startTime = new Time(System.currentTimeMillis());
        FileData fileData = null;
        String path = "/存储空间/2.1.newSeSe";

        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList(path));
        System.out.println("返回的json字符串：");
        System.out.println(resultJson);
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);
        if (fileListResult.isSuccess()) {
            fileData = fileListResult.getData();
            System.out.println(fileData);

            List<FileItem> files = fileData.getFiles();
            System.out.println("测试输出：");
            System.out.println( files.size());
            System.out.println(files);

            fileTree.setName("根目录:"+path);
            fileTree.setPath(path);
            fileTree.setIsdir(true);
            fileTree.setChildrenCount(files.size());
            fileTree.setChildren(files.stream().map(fileItem -> {
                FileTree child = new FileTree();
                child.fileItemToFileTree(fileItem);
                return child;
            }).toList());
            List<FileTree> flatList = new ArrayList<>();
            FileTree.getAllFileTree(fileTree,synologyReadService,flatList);
            Time endTime = new Time(System.currentTimeMillis());
            System.out.println("查询耗时：" + (endTime.getTime() - startTime.getTime()));
            List<FileInfoEntity> entityList = flatList.stream()
                    .map(FileInfoEntity::convertToEntity)
                    .toList();
            long startInsert = System.currentTimeMillis();
            insertBatchSafe(entityList);
            long endInsert = System.currentTimeMillis();
            System.out.println("插入数据库耗时：" + (endInsert - startInsert) + "ms");
        }
    }
    public void previewFileTree(FileTree node, int level) {
        String indent = "---".repeat(level);
        System.out.println(indent + node.getName() + (node.isIsdir() ? "/" : ""));

        if (node.isIsdir() && node.getChildren() != null) {
            for (FileTree child : node.getChildren()) {
                previewFileTree(child, level + 1);
            }
        }
    }
    public void insertBatchSafe(List<FileInfoEntity> list) {
        int batchSize = 100; // 每批100条
        for (int i = 0; i < list.size(); i += batchSize) {
            List<FileInfoEntity> batch = list.subList(i, Math.min(i + batchSize, list.size()));
            fileInfoMapper.insertBatch(batch);
        }
    }



    @Override
    public FileData getFileData(String name) {
        return null;
    }

    @Override
    public FileTree generateFileTree(FileData fileData) {
        List<FileItem> files = fileData.getFiles();
        System.out.println("测试输出：");
        System.out.println( files);
        return null;
    }
}
