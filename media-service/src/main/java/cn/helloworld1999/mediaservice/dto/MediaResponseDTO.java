package cn.helloworld1999.mediaservice.dto;

import lombok.Data;

/**
 * 媒体资源响应DTO
 */
@Data
public class MediaResponseDTO {
    /** 媒体ID */
    private Long id;

    /** 媒体类型：IMAGE/VIDEO */
    private String mediaType;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 上传时间 */
    private String uploadTime;

    /** 文件路径 */
    private String filePath;

    /** 文件名 */
    private String fileName;

    /** 文件扩展名 */
    private String fileExtension;

    /** CDN加速地址 */
    private String cdnUrl;

    /** 存储类型：LOCAL/AWS_S3/ALIYUN_OSS */
    private String storageType;

    /** 存储区域 */
    private String storageRegion;

    /** 是否添加水印 */
    private Boolean watermarkStatus;

    /** 转码状态：PENDING/PROCESSING/COMPLETED/FAILED */
    private String transcodeStatus;
}
