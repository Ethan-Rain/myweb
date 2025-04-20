package cn.helloworld1999.myweb.mapper;

import cn.helloworld1999.myweb.entity.Images;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author yixinrui
* @description 针对表【images(图片表，存储用户上传的图片信息，包含图片的基本属性、状态和分类等)】的数据库操作Mapper
* @createDate 2025-02-21 23:19:51
* @Entity cn.helloworld1999.myweb.entity.Images
*/
@Mapper
public interface ImagesMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Images record);

    int insertSelective(Images record);

    Images selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Images record);

    int updateByPrimaryKey(Images record);

    // 随机从表中抽取一条数据
    Images selectRandomImage();
    Integer existsByImageUrl(String imageUrl);

    List<Images> getRandomImages(int count);

    List<Images> getRandomImagesWithConditions(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore, @Param("category") String category, @Param("status") String status, @Param("limit") Integer limit);

}
