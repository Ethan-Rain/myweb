package cn.helloworld1999.myweb.service.impl;

import cn.helloworld1999.myweb.entity.Images;
import cn.helloworld1999.myweb.exception.BusinessException;
import cn.helloworld1999.myweb.mapper.ImagesMapper;
import cn.helloworld1999.myweb.service.ImagesService;
import cn.helloworld1999.myweb.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ImagesServiceImpl implements ImagesService {
    /**
     * 图片数据访问对象
     */
    @Autowired
    private ImagesMapper imagesMapper;
    
    @Autowired
    private ImageUtils imageUtils;

    /**
     * 根据ID获取单张图片信息
     * @param id 图片ID
     * @return 图片实体对象
     */
    @Override
    public Images getImage(Long id) {
        Images image = imagesMapper.selectByPrimaryKey(id);
        if (image == null) {
            throw BusinessException.notFound("图片");
        }
        return image;
    }

    /**
     * 将图片文件转换为Base64编码字符串
     * @param filePath 图片文件路径
     * @return Base64编码的字符串
     * @throws IOException 文件读取异常
     * @throws BusinessException 文件路径无效时抛出
     */
    public String convertImageToBase64(String filePath) throws IOException {
        return imageUtils.convertImageToBase64(filePath);
    }

    /**
     * 获取图片详细信息
     * @param id 图片ID
     * @return 图片实体对象
     */
    @Override
    public Images getImageInfo(Long id) {
        Images image = imagesMapper.selectByPrimaryKey(id);
        if (image == null) {
            throw BusinessException.notFound("图片");
        }
        return image;
    }

    /**
     * 获取随机图片的Base64编码
     * @return Base64编码的图片字符串
     * @throws IOException 文件读取异常
     */
    @Override
    public String getRandomImage() throws IOException {
        Images randomImage = imagesMapper.selectRandomImage();
        if (randomImage == null) {
            throw new BusinessException("没有可用的随机图片");
        }
        return convertImageToBase64(randomImage.getImageUrl());
    }

    /**
     * 扫描指定目录下的图片文件并添加到数据库
     * @param directoryPath 需要扫描的目录路径
     * @return 新增的图片数量
     * @throws IOException 目录访问异常
     * @throws BusinessException 目录路径无效时抛出
     */
    @Override
    public Integer scanImages(String directoryPath) throws IOException {
        if (!StringUtils.hasText(directoryPath)) {
            throw new BusinessException("目录路径不能为空");
        }
        
        Path root = Paths.get(directoryPath);
        if (!Files.isDirectory(root)) {
            throw new BusinessException("无效的目录路径: " + directoryPath);
        }

        AtomicInteger counter = new AtomicInteger();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    if (imageUtils.isValidImage(file)) {
                        processImage(file, counter);
                    }
                } catch (Exception e) {
                    log.error("处理文件 {} 失败: {}", file, e.getMessage(), e);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return counter.get();
    }

    /**
     * 获取随机图片信息
     * @return 随机图片的实体对象
     * @throws IOException 数据访问异常
     */
    @Override
    public Images getRandomImageInfo() throws IOException {
        Images image = imagesMapper.selectRandomImage();
        if (image == null) {
            throw new BusinessException("没有可用的随机图片");
        }
        return image;
    }

    /**
     * 获取指定数量的随机图片
     * @param count 需要获取的图片数量
     * @return 图片实体对象列表
     */
    @Override
    public List<Images> getRandomImages(int count) {
        if (count <= 0) {
            throw new BusinessException("请求的图片数量必须大于0");
        }
        return imagesMapper.getRandomImages(count);
    }

    /**
     * 根据条件获取随机图片
     * @param minScore 最低评分
     * @param maxScore 最高评分
     * @param category 图片分类
     * @param status 图片状态
     * @param limit 返回数量限制
     * @return 符合条件的图片列表
     */
    @Override
    public List<Images> getRandomImagesWithConditions(Integer minScore, Integer maxScore, String category, String status, Integer limit) {
        // 设置合理的默认值
        limit = (limit == null || limit <= 0) ? 10 : limit;
        return imagesMapper.getRandomImagesWithConditions(minScore, maxScore, category, status, limit);
    }

    /**
     * 处理单个图片文件
     * @param file 图片文件路径
     * @param counter 计数器，用于统计处理的图片数量
     * @throws IOException 文件处理异常
     */
    private void processImage(Path file, AtomicInteger counter) throws IOException {
        String filePath = file.toAbsolutePath().toString();
        if (imagesMapper.existsByImageUrl(filePath) == null) {
            try {
                Images image = new Images()
                        .setImageUrl(filePath)
                        .setUserId(1L) // 默认用户ID
                        .setFileName(file.getFileName().toString())
                        .setSize(Files.size(file))
                        .setFormat(imageUtils.parseFileExtension(file))
                        .setResolution(imageUtils.getImageResolution(filePath))
                        .setStatus("pending")
                        .setCreatedAt(new Date())
                        .setUpdatedAt(new Date());
                imagesMapper.insertSelective(image);
                counter.incrementAndGet();
                log.debug("添加图片: {}", filePath);
            } catch (Exception e) {
                log.error("处理图片失败: {}, 错误: {}", filePath, e.getMessage(), e);
            }
        }
    }
}