package cn.helloworld1999.synology.bean;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class FileTree {

    @Autowired
    private SynologyReadService synologyReadService;

    private boolean isdir;  // 是否目录
    private String name;    // 名称
    private String path;    // 路径
    private int childrenCount;   // 子节点数量
    private List<FileTree> children; // 子节点列表

    public FileTree fileItemToFileTree(FileItem fileItem) {
        this.isdir = fileItem.isIsdir();
        this.name = fileItem.getName();
        this.path = fileItem.getPath();
        return this;
    }

    /**
     * 递归构建文件树
     */
    public FileTree getAllFileTree(FileTree current) {
        if (!current.isdir) {
            return current; // 文件直接返回
        }

        // 获取当前目录下的文件列表
        String resultJson = JSONUtil.toJsonStr(synologyReadService.getFileList(current.getPath()));
        FileListResult fileListResult = JSONUtil.toBean(resultJson, FileListResult.class);

        if (!fileListResult.isSuccess()) {
            return current;
        }

        List<FileItem> files = fileListResult.getData().getFiles();
        List<FileTree> children = files.stream().map(fileItem -> {
            FileTree child = new FileTree();
            child.fileItemToFileTree(fileItem);
            return getAllFileTree(child);
        }).toList();

        current.setChildren(children);
        current.setChildrenCount(children.size());
        return current;
    }
}
