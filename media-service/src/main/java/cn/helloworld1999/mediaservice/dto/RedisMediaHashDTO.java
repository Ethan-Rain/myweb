package cn.helloworld1999.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于存储 Redis 中媒体信息的 Hash 结构 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMediaHashDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 媒体ID
    private Long id;

    // 媒体状态
    private String status;

    // 媒体类型（IMAGE/VIDEO）
    private String mediaType;

    // 媒体标题
    private String title;

    // 媒体描述
    private String description;

    // 分类ID
    private Long categoryId;

    // 文件大小（字节）
    private Long size;

    // 文件名
    private String fileName;

    // 文件路径
    private String filePath;

    // 文件扩展名
    private String fileExtension;

    // 获取完整的文件路径（包含文件名）
    public String getFullPath() {
        if (filePath == null || fileName == null) {
            return null;
        }
        return filePath.endsWith("/") ? filePath + fileName : filePath + "/" + fileName;
    }

    // 获取带扩展名的完整文件名
    public String getFullFileName() {
        if (fileName == null || fileExtension == null) {
            return fileName;
        }
        return fileName + (fileExtension.startsWith(".") ? "" : ".") + fileExtension;
    }
}