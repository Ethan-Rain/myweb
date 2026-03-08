package cn.helloworld1999.synology.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileData {
    private List<FileItem> files;  // 这里就是你说的数组（files数组）
    private int offset;
    private int total;
}
