package cn.helloworld1999.myweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片表，存储用户上传的图片信息，包含图片的基本属性、状态和分类等
 * @TableName images
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Images implements Serializable {
    /**
     * 图片ID，主键，自增
     */
    private Long id;

    /**
     * 上传者用户ID，外键关联用户表
     */
    private Long userId;

    /**
     * 图片URL地址
     */
    private String imageUrl;

    /**
     * 上传时的原始文件名
     */
    private String fileName;

    /**
     * 图片标题
     */
    private String title;

    /**
     * 图片描述
     */
    private String description;

    /**
     * 图片分类，表示图片的类型，例如：风景、人物、建筑等
     */
    private String category;

    /**
     * 图片状态，pending表示待审核，approved表示已审核，rejected表示审核未通过
     */
    private String status;

    /**
     * 图片文件大小，单位为字节
     */
    private Long size;

    /**
     * 图片格式，例如：jpg、png、gif等
     */
    private String format;

    /**
     * 图片分辨率，例如：1920x1080
     */
    private String resolution;

    /**
     * 图片类型，normal为普通图片，thumbnail为缩略图，poster为海报图等
     */
    private String imageType;

    /**
     * 图片来源，表示图片上传的来源平台或方式
     */
    private String source;

    /**
     * 图片上传时间
     */
    private Date createdAt;

    /**
     * 图片更新时间
     */
    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Images other = (Images) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getImageUrl() == null ? other.getImageUrl() == null : this.getImageUrl().equals(other.getImageUrl()))
            && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getFormat() == null ? other.getFormat() == null : this.getFormat().equals(other.getFormat()))
            && (this.getResolution() == null ? other.getResolution() == null : this.getResolution().equals(other.getResolution()))
            && (this.getImageType() == null ? other.getImageType() == null : this.getImageType().equals(other.getImageType()))
            && (this.getSource() == null ? other.getSource() == null : this.getSource().equals(other.getSource()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getImageUrl() == null) ? 0 : getImageUrl().hashCode());
        result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getFormat() == null) ? 0 : getFormat().hashCode());
        result = prime * result + ((getResolution() == null) ? 0 : getResolution().hashCode());
        result = prime * result + ((getImageType() == null) ? 0 : getImageType().hashCode());
        result = prime * result + ((getSource() == null) ? 0 : getSource().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", imageUrl=").append(imageUrl);
        sb.append(", fileName=").append(fileName);
        sb.append(", title=").append(title);
        sb.append(", description=").append(description);
        sb.append(", category=").append(category);
        sb.append(", status=").append(status);
        sb.append(", size=").append(size);
        sb.append(", format=").append(format);
        sb.append(", resolution=").append(resolution);
        sb.append(", imageType=").append(imageType);
        sb.append(", source=").append(source);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}