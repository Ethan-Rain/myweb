package cn.helloworld1999.myweb.mapper;
import cn.helloworld1999.myweb.demos.api.model.ImageMetadata;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yixinrui
* @description 针对表【image_metadata】的数据库操作Mapper
* @createDate 2025-02-19 02:31:52
* @Entity cn.helloworld1999.myweb.demos.api.model.ImageMetadata
*/
@Mapper
public interface ImageMetadataMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ImageMetadata record);

    int insertSelective(ImageMetadata record);

    ImageMetadata selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ImageMetadata record);

    int updateByPrimaryKey(ImageMetadata record);

}