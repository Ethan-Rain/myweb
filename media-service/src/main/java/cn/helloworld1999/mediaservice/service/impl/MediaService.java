package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.entity.Media;
import cn.helloworld1999.mediaservice.entity.MediaContent;
import cn.helloworld1999.mediaservice.mapper.MediaContentMapper;
import cn.helloworld1999.mediaservice.mapper.MediaMapper;
import cn.helloworld1999.mediaservice.service.IMediaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 媒体服务实现
 */
@Service
public class MediaService extends ServiceImpl<MediaMapper, Media> implements IMediaService {
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private MediaContentMapper mediaContentMapper;

    @Autowired
            private MediaMapper mediaMapper;
    
    Logger logger = LoggerFactory.getLogger(MediaService.class);

    public List<String> getAllVideos(Long category) {
        try {
            logger.info("开始查询视频数据，分类ID: {}", category);
            
            // 使用联表查询获取视频路径
            QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("media_content.file_path")
                    .inSql("media_id", "SELECT id FROM media WHERE media_type = 'VIDEO' AND category_id = " + category + " AND status = 'ACTIVE'");
            List<MediaContent> contents = mediaContentMapper.selectList(queryWrapper);
            
            logger.info("查询到视频总数: {}", contents.size());
            
            List<String> videoPaths = new ArrayList<>();
            for (MediaContent content : contents) {
                if (content.getFilePath() != null) {
                    videoPaths.add(content.getFilePath());
                }
            }
            
            return videoPaths;
        } catch (Exception e) {
            logger.error("获取视频列表失败", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllImages(Long category) {
        try {
            logger.info("开始查询图片数据，分类ID: {}", category);
            
            // 使用联表查询获取图片路径
            QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("media_content.file_path")
                    .inSql("media_id", "SELECT id FROM media WHERE media_type = 'IMAGE' AND category_id = " + category + " AND status = 'ACTIVE'");
            List<MediaContent> contents = mediaContentMapper.selectList(queryWrapper);
            
            logger.info("查询到图片总数: {}", contents.size());
            
            List<String> imagePaths = new ArrayList<>();
            for (MediaContent content : contents) {
                if (content.getFilePath() != null) {
                    imagePaths.add(content.getFilePath());
                }
            }
            
            return imagePaths;
        } catch (Exception e) {
            logger.error("获取图片列表失败", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getRandomVideos(Long category) {
        try {
            logger.info("开始获取随机视频，分类ID: {}", category);
            
            // 从Redis获取所有视频
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String cacheKey = "cache:videos:" + category;
            logger.info("尝试从Redis获取缓存，key: {}", cacheKey);
            
            List<String> allVideos = (List<String>) ops.get(cacheKey);
            if (CollectionUtils.isEmpty(allVideos)) {
                logger.info("Redis中未找到缓存，从数据库获取视频...");
                // 如果Redis中没有数据，从数据库获取
                allVideos = getAllVideos(category);
                logger.info("从数据库获取到 {} 个视频", allVideos != null ? allVideos.size() : 0);
                
                // 将结果存入Redis
                if (!CollectionUtils.isEmpty(allVideos)) {
                    logger.info("将视频数据存入Redis，key: {}", cacheKey);
                    ops.set(cacheKey, allVideos, 1, TimeUnit.HOURS);
                }
            } else {
                logger.info("从Redis获取到 {} 个视频", allVideos.size());
            }
            
            if (CollectionUtils.isEmpty(allVideos)) {
                logger.warn("未找到视频数据，分类ID: {}", category);
                return Collections.emptyList();
            }
            
            // 随机选择一个视频
            int randomIndex = new Random().nextInt(allVideos.size());
            String selectedVideo = allVideos.get(randomIndex);
            logger.info("随机选择第 {} 个视频: {}", randomIndex, selectedVideo);
            
            return Collections.singletonList(selectedVideo);
        } catch (Exception e) {
            logger.error("获取随机视频失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取指定分类下的随机图片
     * @param category 分类ID
     * @return 随机图片列表
     */
    public List<String> getRandomImages(Long category) {
        try {
            logger.info("开始获取随机图片，分类ID: {}", category);
            
            // 从Redis获取所有图片
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String cacheKey = "cache:images:" + category;
            logger.info("尝试从Redis获取缓存，key: {}", cacheKey);
            
            List<String> allImages = (List<String>) ops.get(cacheKey);
            if (CollectionUtils.isEmpty(allImages)) {
                logger.info("Redis中未找到缓存，从数据库获取图片...");
                // 如果Redis中没有数据，从数据库获取
                allImages = getAllImages(category);
                logger.info("从数据库获取到 {} 张图片", allImages != null ? allImages.size() : 0);
                
                // 将结果存入Redis
                if (!CollectionUtils.isEmpty(allImages)) {
                    logger.info("将图片数据存入Redis，key: {}", cacheKey);
                    ops.set(cacheKey, allImages, 1, TimeUnit.HOURS);
                }
            } else {
                logger.info("从Redis获取到 {} 张图片", allImages.size());
            }
            
            if (CollectionUtils.isEmpty(allImages)) {
                logger.warn("未找到图片数据，分类ID: {}", category);
                return Collections.emptyList();
            }
            
            // 随机选择一张图片
            int randomIndex = new Random().nextInt(allImages.size());
            String selectedImage = allImages.get(randomIndex);
            logger.info("随机选择第 {} 张图片: {}", randomIndex, selectedImage);
            
            return Collections.singletonList(selectedImage);
        } catch (Exception e) {
            logger.error("获取随机图片失败", e);
            return Collections.emptyList();
        }
    }
    public Map<String,Object> getRandomMediaInfoJSON(Map<String,Object> queryInfo){
        String cacheKey = "media:" + queryInfo.get("type") + ":random";
        Map<String,Object> result = new HashMap<>();
        Object value = null;
        // 1. 从缓存随机获取
        Long size = redisTemplate.opsForList().size(cacheKey);
        if (size != null && size > 0) {
            int randomIndex = new Random().nextInt(size.intValue());
            result = (Map<String, Object>) redisTemplate.opsForList().index(cacheKey, randomIndex);
            value = result.get("file_path");
            value = ((String) value).replace("\\\\192.168.31.105\\centos_share\\storage\\", "http://192.168.31.105:5555/storage/");
            value = ((String) value).replace("\\", "/");
            result.put("file_path", value);
            return result;
        }
        
        // 2. 缓存未命中则查询数据库
        List<Map<String,Object>> mediaList = mediaMapper.selectAllByType(queryInfo.get("type").toString());
        
        // 3. 查询结果非空才缓存
        if (mediaList != null && !mediaList.isEmpty()) {
            // 使用列表结构存储
            redisTemplate.opsForList().leftPushAll(cacheKey, mediaList.toArray());
            redisTemplate.expire(cacheKey, 1, TimeUnit.HOURS);
            
            // 随机返回一个
            result = mediaList.get(new Random().nextInt(mediaList.size()));
            value = result.get("file_path");
            value = ((String) value).replace("\\\\192.168.31.105\\centos_share\\storage\\", "http://192.168.31.105:5555/storage/");
            value = ((String) value).replace("\\", "/");
            result.put("file_path", value);
            return result;
        }
        return Collections.emptyMap();
    }
    public List<Map<String,Object>>queryResourcesInTheSameFolder(String path){
        List<Map<String,Object>> result = new ArrayList<>();
        path = path.replace("http://192.168.31.105:5555/storage", "").replace("/", "\\").split("\\\\")[2];
        result = mediaMapper.queryResourcesInTheSameFolder(path);
        for (Map<String, Object> item : result) {
            Object value = item.get("file_path");
            value = ((String) value).replace("\\\\192.168.31.105\\centos_share\\storage\\", "http://192.168.31.105:5555/storage/");
            value = ((String) value).replace("\\", "/");
            item.put("file_path", value);
        }
    return result;
    }
}