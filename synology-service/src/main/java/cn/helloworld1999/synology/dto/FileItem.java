package cn.helloworld1999.synology.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileItem {
    private boolean isdir;
    private String name;
    private String path;
    private Additional additional; // 新加
}
