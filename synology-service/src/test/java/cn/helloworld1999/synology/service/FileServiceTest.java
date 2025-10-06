package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.bean.FileData;
import cn.helloworld1999.synology.bean.FileItem;
import cn.helloworld1999.synology.bean.FileListResult;
import cn.helloworld1999.synology.bean.FileTree;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SpringBootTest
public class FileServiceTest implements IFileService {
    @Autowired
    public SynologyReadService synologyReadService;

    @Test
    public void getFileData() {
        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList("/存储空间"));
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);
        if (fileListResult.isSuccess()) {
            FileData fileData = fileListResult.getData();
            System.out.println(fileData);
        }
    }

    @Autowired
    FileTree fileTree;
    @Test
    public void getFileTree(){
        FileData fileData = null;
        String path = "/存储空间/14.书籍";

        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList(path));
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);
        if (fileListResult.isSuccess()) {
            fileData = fileListResult.getData();
            System.out.println(fileData);

            List<FileItem> files = fileData.getFiles();
            System.out.println("测试输出：");
            System.out.println( files.size());

            fileTree.setName("根目录");
            fileTree.setPath(path);
            fileTree.setIsdir(true);
            fileTree.setChildrenCount(files.size());
            fileTree.setChildren(files.stream().map(fileItem -> {
                FileTree child = new FileTree();
                child.fileItemToFileTree(fileItem);
                return child;
            }).toList());
            FileTree fileTree1 = fileTree.getAllFileTree(fileTree);
            System.out.println(fileTree1);
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
