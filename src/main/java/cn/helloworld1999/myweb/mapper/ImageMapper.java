package cn.helloworld1999.myweb.mapper;
import cn.helloworld1999.myweb.demos.api.model.Image;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author yixinrui
* @description 针对表【image】的数据库操作Mapper
* @createDate 2025-02-19 02:31:52
* @Entity cn.helloworld1999.myweb.demos.api.model.Image
*/
@Mapper
public interface ImageMapper {

    int deleteByPrimaryKey(String id);

    int insert(Image record);

    int insertSelective(Image record);

    Image selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Image record);

    int updateByPrimaryKey(Image record);

    int countAllImages();

    Image selectRandomImage();

}