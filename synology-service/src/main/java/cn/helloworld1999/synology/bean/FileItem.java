package cn.helloworld1999.synology.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileItem {
    private boolean isdir;  // 对应每个文件的isdir字段
    private String name;    // 对应每个文件的name字段
    private String path;    // 对应每个文件的path字段
}
