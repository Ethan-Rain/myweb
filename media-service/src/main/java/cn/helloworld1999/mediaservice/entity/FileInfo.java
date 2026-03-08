package cn.helloworld1999.mediaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_info")
public class FileInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String path;
    private String name;
    private Long size;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String fileHash;
    private Long category;
    private Boolean isDirectory;
    private String mimeType;
    private LocalDateTime scanTime;
}
