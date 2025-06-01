package cn.helloworld1999.mediaservice.mapper;

import cn.helloworld1999.mediaservice.entity.MediaContent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 媒体内容Mapper
 */
@Mapper
public interface MediaContentMapper extends BaseMapper<MediaContent> {
    // 可以在这里添加自定义的SQL查询方法
}
