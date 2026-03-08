package cn.helloworld1999.mediaservice.dto;

import cn.helloworld1999.mediaservice.entity.FileInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 扫描结果响应DTO
 */
@Data
@Accessors(chain = true)
public class ScanResultDTO {
    /** 扫描状态：SUCCESS/FAILURE */
    private String status;

    /** 错误信息（如果失败） */
    private String errorMessage;

    /** 扫描统计信息 */
    private ScanStats stats;

    /** 失败的文件列表 */
    private List<String> failedFiles;

    /** 存储的文件信息 */
    private List<Map<String, Object>> storedFiles;

    /** V4版本新增：文件实体列表 */
    private List<FileInfo> fileList;

    /** V4版本新增：文件总大小 */
    private long totalSize;

    @Data
    public static class ScanStats {
        /** 总文件数 */
        private int totalFiles;

        /** 成功处理数 */
        private int successCount;

        /** 失败数 */
        private int failureCount;

        /** 跳过数（已存在） */
        private int skippedCount;

        /** 空文件数 */
        private int emptyFiles;

        /** 非支持文件数 */
        private int unsupportedFiles;
    }
}
