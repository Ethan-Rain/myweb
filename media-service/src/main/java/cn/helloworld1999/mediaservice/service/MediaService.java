package cn.helloworld1999.mediaservice.service;

import cn.helloworld1999.mediaservice.entity.Media;
import cn.helloworld1999.mediaservice.entity.MediaContent;
import cn.helloworld1999.mediaservice.mapper.MediaContentMapper;
import cn.helloworld1999.mediaservice.mapper.MediaMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 媒体服务实现
 */
@Service
public class MediaService extends ServiceImpl<MediaMapper, Media> implements IMediaService {

    @Autowired
    private MediaContentMapper mediaContentMapper;

    /**
     * 获取指定分类下的随机视频
     * @param category 分类ID
     * @return 随机视频列表
     */
    public List<String> getRandomVideos(String category) {
        // 查询指定分类下的所有视频
        List<Media> mediaList = baseMapper.selectList(
            new QueryWrapper<Media>()
                .eq("media_type", "VIDEO")
                .eq("category_id", category)
                .eq("status", "ACTIVE")
        );

        if (mediaList.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取对应的媒体内容
        List<String> videoPaths = new ArrayList<>();
        for (Media media : mediaList) {
            MediaContent content = mediaContentMapper.selectById(media.getId());
            if (content != null) {
                videoPaths.add(content.getFilePath());
            }
        }

        return videoPaths;
    }

    /**
     * 获取指定分类下的随机图片
     * @param category 分类ID
     * @return 随机图片列表
     */
    public List<String> getRandomImages(Integer category) {
        // 查询指定分类下的所有图片
        List<Media> mediaList = baseMapper.selectList(
            new QueryWrapper<Media>()
                .eq("media_type", "IMAGE")
                .eq("category_id", category)
                .eq("status", "ACTIVE")
        );

        if (mediaList.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取对应的媒体内容
        List<String> imagePaths = new ArrayList<>();
        for (Media media : mediaList) {
            MediaContent content = mediaContentMapper.selectById(media.getId());
            if (content != null) {
                imagePaths.add(content.getFilePath());
            }
        }

        return imagePaths;
    }
}
