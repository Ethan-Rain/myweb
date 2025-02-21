package cn.helloworld1999.myweb.mapper;

import cn.helloworld1999.myweb.entity.Ratings;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yixinrui
* @description 针对表【ratings(评分表，存储用户对图片的评分记录)】的数据库操作Mapper
* @createDate 2025-02-21 23:19:51
* @Entity cn.helloworld1999.myweb.entity.Ratings
*/
@Mapper
public interface RatingsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Ratings record);

    int insertSelective(Ratings record);

    Ratings selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Ratings record);

    int updateByPrimaryKey(Ratings record);

}
