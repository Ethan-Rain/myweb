package cn.helloworld1999.mediaservice.mapper;

import cn.helloworld1999.mediaservice.entity.MediaContent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 媒体内容Mapper
 */
@Mapper
public interface MediaContentMapper extends BaseMapper<MediaContent> {
    @Select("SELECT * FROM media_content WHERE media_id = #{mediaId}")
    MediaContent selectByMediaId(Long mediaId);
}
