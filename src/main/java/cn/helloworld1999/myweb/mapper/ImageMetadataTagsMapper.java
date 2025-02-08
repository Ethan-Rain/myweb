package cn.helloworld1999.myweb.mapper;
import cn.helloworld1999.myweb.demos.api.model.ImageMetadataTags;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yixinrui
* @description 针对表【image_metadata_tags】的数据库操作Mapper
* @createDate 2025-02-19 02:31:52
* @Entity cn.helloworld1999.myweb.demos.api.model.ImageMetadataTags
*/
@Mapper
public interface ImageMetadataTagsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ImageMetadataTags record);

    int insertSelective(ImageMetadataTags record);

    ImageMetadataTags selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ImageMetadataTags record);

    int updateByPrimaryKey(ImageMetadataTags record);

}
