package cn.helloworld1999.synology.service;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.dto.FileData;
import cn.helloworld1999.synology.dto.FileItem;
import cn.helloworld1999.synology.dto.FileListResult;
import cn.helloworld1999.synology.bean.FileTree;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.sql.Time;
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

    FileTree fileTree = new FileTree();
    @Test
    public void getFileTree(){
        Time startTime = new Time(System.currentTimeMillis());
        FileData fileData = null;
        String path = "/存储空间/2.1.newSeSe/千阳长离";

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

            fileTree.setName("根目录");
            fileTree.setPath(path);
            fileTree.setIsdir(true);
            fileTree.setChildrenCount(files.size());
            fileTree.setChildren(files.stream().map(fileItem -> {
                FileTree child = new FileTree();
                child.fileItemToFileTree(fileItem);
                return child;
            }).toList());
            FileTree fullTree = FileTree.getAllFileTree(fileTree,synologyReadService);
            Time endTime = new Time(System.currentTimeMillis());
            Time printTime = new Time(System.currentTimeMillis());
            System.out.println("打印文件树：");
            System.out.println(fullTree);
            previewFileTree(fullTree, 0);
            System.out.println("打印耗时：" + (printTime.getTime() - endTime.getTime()));
            System.out.println("查询耗时：" + (endTime.getTime() - startTime.getTime()));
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
