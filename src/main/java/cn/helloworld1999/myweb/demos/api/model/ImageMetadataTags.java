package cn.helloworld1999.myweb.demos.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName image_metadata_tags
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageMetadataTags {
    /**
     * 
     */
    private String imageId;

    /**
     * 
     */
    private String tag;

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
        ImageMetadataTags other = (ImageMetadataTags) that;
        return (this.getImageId() == null ? other.getImageId() == null : this.getImageId().equals(other.getImageId()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getImageId() == null) ? 0 : getImageId().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", imageId=").append(imageId);
        sb.append(", tag=").append(tag);
        sb.append("]");
        return sb.toString();
    }
}