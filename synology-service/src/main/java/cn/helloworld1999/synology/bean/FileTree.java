package cn.helloworld1999.synology.bean;

import cn.helloworld1999.synology.api.SynologyReadService;
import cn.helloworld1999.synology.dto.Additional;
import cn.helloworld1999.synology.dto.FileItem;
import cn.helloworld1999.synology.dto.FileListResult;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTree {

    private boolean isdir;              // 是否目录
    private String name;                // 名称
    private String path;                // 路径
    private Additional additional;      // 详细信息
    private int childrenCount;          // 子节点数量
    private List<FileTree> children;    // 子节点列表

    /**
     * 将 FileItem 转换为 FileTree
     */
    public FileTree fileItemToFileTree(FileItem fileItem) {
        this.isdir = fileItem.isIsdir();
        this.name = fileItem.getName();
        this.path = fileItem.getPath();
        this.additional = fileItem.getAdditional();
        return this;
    }

    /**
     * 递归构建文件树
     * @param current 当前节点
     * @param service Synology API 服务
     * @return 构建好的 FileTree
     */
    public static FileTree getAllFileTree(FileTree current, SynologyReadService service) {
        if (!current.isdir) {
            return current; // 文件直接返回
        }

        // 1. 先获取 Map
        Map<String, Object> resultMap = service.getFileList(current.getPath());

        // 2. 转成 JSON 字符串
        String jsonStr = JSONUtil.toJsonStr(resultMap);

        // 3. JSON 字符串转对象
        FileListResult fileListResult = JSONUtil.toBean(jsonStr, FileListResult.class);

        if (!fileListResult.isSuccess()) {
            return current;
        }

        List<FileItem> files = fileListResult.getData().getFiles();
        List<FileTree> children = files.stream()
                .map(fileItem -> {
                    FileTree child = new FileTree();
                    child.fileItemToFileTree(fileItem);
                    // 递归
                    return getAllFileTree(child, service);
                })
                .toList();

        current.setChildren(children);
        current.setChildrenCount(children.size());
        return current;
    }
}
