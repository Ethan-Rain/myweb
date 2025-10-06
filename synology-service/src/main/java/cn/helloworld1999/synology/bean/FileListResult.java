package cn.helloworld1999.synology.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileListResult {
    private boolean success;  // 对应JSON的success字段
    private FileData data;    // 对应JSON的data字段
}
