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
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media")
@AllArgsConstructor
@NoArgsConstructor
public class Media implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID（雪花ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 媒体类型：IMAGE/VIDEO */
    private String mediaType;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 上传时间 */
    private LocalDateTime uploadTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 状态：ACTIVE/DELETED */
    private String status;

    /** 分类ID */
    private Long categoryId;

    /** 上传用户ID */
    private Long userId;

    /** 文件大小（字节） */
    private Long size;

    /** 时长（秒） */
    private Integer duration;

    /** 缩略图ID */
    private Long thumbnailId;

    /** 标签列表 */
    private List<String> tags;

    /** 元数据 */
    private String metadata;
}
