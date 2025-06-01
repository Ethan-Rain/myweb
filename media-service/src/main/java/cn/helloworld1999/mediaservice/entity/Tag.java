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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tag")
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID（雪花ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 标签名称 */
    private String name;

    /** 标签描述 */
    private String description;

    /** 状态：ACTIVE/DELETED */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
