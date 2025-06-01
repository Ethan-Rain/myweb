package cn.helloworld1999.mediaservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media_content")
@AllArgsConstructor
@NoArgsConstructor
public class MediaContent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID（雪花ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 媒体ID */
    private Long mediaId;

    /** 文件存储路径 */
    private String filePath;

    /** 文件名 */
    private String fileName;

    /** 文件扩展名 */
    private String fileExtension;

    /** 存储类型：LOCAL/AWS_S3/ALIYUN_OSS */
    private String storageType;

    /** 存储区域 */
    private String storageRegion;

    /** CDN加速地址 */
    private String cdnUrl;

    /** 是否添加水印 */
    private Boolean watermarkStatus;

    /** 转码状态：PENDING/PROCESSING/COMPLETED/FAILED */
    private String transcodeStatus;

    /** 转码配置 */
    private String transcodeProfile;
}
