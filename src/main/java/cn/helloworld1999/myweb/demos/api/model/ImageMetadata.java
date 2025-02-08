package cn.helloworld1999.myweb.demos.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName image_metadata
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageMetadata {
    private String imageId;  // 类型为 String

    /**
     * 
     */
    private Double rating;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private String uploader;

    /**
     * 
     */
    private Long uploadTime;

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
        ImageMetadata other = (ImageMetadata) that;
        return (this.getImageId() == null ? other.getImageId() == null : this.getImageId().equals(other.getImageId()))
            && (this.getRating() == null ? other.getRating() == null : this.getRating().equals(other.getRating()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getUploader() == null ? other.getUploader() == null : this.getUploader().equals(other.getUploader()))
            && (this.getUploadTime() == null ? other.getUploadTime() == null : this.getUploadTime().equals(other.getUploadTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getImageId() == null) ? 0 : getImageId().hashCode());
        result = prime * result + ((getRating() == null) ? 0 : getRating().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getUploader() == null) ? 0 : getUploader().hashCode());
        result = prime * result + ((getUploadTime() == null) ? 0 : getUploadTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", imageId=").append(imageId);
        sb.append(", rating=").append(rating);
        sb.append(", description=").append(description);
        sb.append(", uploader=").append(uploader);
        sb.append(", uploadTime=").append(uploadTime);
        sb.append("]");
        return sb.toString();
    }
}