package cn.helloworld1999.myweb.demos.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName image
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    /**
     * 
     */
    private String id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String path;

    /**
     * 
     */
    private String contentType;

    /**
     * 
     */
    private Long size;

    /**
     * 
     */
    private Integer width;

    /**
     * 
     */
    private Integer height;

    /**
     * 
     */
    private String format;

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
        Image other = (Image) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getContentType() == null ? other.getContentType() == null : this.getContentType().equals(other.getContentType()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getWidth() == null ? other.getWidth() == null : this.getWidth().equals(other.getWidth()))
            && (this.getHeight() == null ? other.getHeight() == null : this.getHeight().equals(other.getHeight()))
            && (this.getFormat() == null ? other.getFormat() == null : this.getFormat().equals(other.getFormat()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getContentType() == null) ? 0 : getContentType().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getWidth() == null) ? 0 : getWidth().hashCode());
        result = prime * result + ((getHeight() == null) ? 0 : getHeight().hashCode());
        result = prime * result + ((getFormat() == null) ? 0 : getFormat().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", path=").append(path);
        sb.append(", contentType=").append(contentType);
        sb.append(", size=").append(size);
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", format=").append(format);
        sb.append("]");
        return sb.toString();
    }
}