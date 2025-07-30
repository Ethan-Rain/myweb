package cn.helloworld1999.mediaservice.mapper;

import cn.helloworld1999.mediaservice.entity.Media;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 媒体Mapper
 */
@Mapper
public interface MediaMapper extends BaseMapper<Media> {
    // 可以在这里添加自定义的SQL查询方法
    // TODO 这里暂时指定一下IP 看看效果
    @Select("SELECT * FROM media AS m INNER JOIN media_content mc on m.id = mc.media_id WHERE media_type = #{mediaType} AND mc.file_path LIKE '%192.168.31.105%'")
    List<Map<String,Object>> selectAllByType(String mediaType);
    @Select("SELECT * FROM media AS m INNER JOIN media_content mc on m.id = mc.media_id WHERE mc.file_path LIKE CONCAT(CONCAT('%',#{path}),'%') AND mc.file_path LIKE '%192.168.31.105%'")
    List<Map<String,Object>> queryResourcesInTheSameFolder(String path);
}