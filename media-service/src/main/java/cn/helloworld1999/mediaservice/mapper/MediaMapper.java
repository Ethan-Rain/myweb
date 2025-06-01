package cn.helloworld1999.mediaservice.mapper;

import cn.helloworld1999.mediaservice.entity.Media;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 媒体Mapper
 */
@Mapper
public interface MediaMapper extends BaseMapper<Media> {
    // 可以在这里添加自定义的SQL查询方法
}
