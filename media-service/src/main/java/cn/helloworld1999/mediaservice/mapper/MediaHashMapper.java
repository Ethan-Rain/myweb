package cn.helloworld1999.mediaservice.mapper;

import cn.helloworld1999.mediaservice.dto.RedisMediaHashDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MediaHashMapper extends BaseMapper<RedisMediaHashDTO> {

    /**
     * 根据分类ID查询媒体信息
     * @param categoryId 分类ID
     * @return 媒体信息列表
     */
    @Select("""
        SELECT 
            m.id,
            m.status,
            m.media_type as mediaType,
            m.title,
            m.description,
            m.category_id as categoryId,
            m.size,
            mc.file_name as fileName,
            mc.file_path as filePath,
            mc.file_extension as fileExtension
        FROM media AS m 
        INNER JOIN media_content AS mc ON m.id = mc.media_id
        WHERE m.category_id = #{categoryId}
        AND m.status = 'ACTIVE'
    """)
    List<RedisMediaHashDTO> findMediaByCategory(@Param("categoryId") Long categoryId);

    /**
     * 查询所有媒体信息
     * @return 媒体信息列表
     */
    @Select("""
        SELECT 
            m.id,
            m.status,
            m.media_type as mediaType,
            m.title,
            m.description,
            m.category_id as categoryId,
            m.size,
            mc.file_name as fileName,
            mc.file_path as filePath,
            mc.file_extension as fileExtension
        FROM media AS m 
        INNER JOIN media_content AS mc ON m.id = mc.media_id
        WHERE m.status = 'ACTIVE'
    """)
    List<RedisMediaHashDTO> findAllActiveMedia();
}