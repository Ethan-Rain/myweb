package cn.helloworld1999.myweb.controller;

import cn.helloworld1999.myweb.dto.ResponseResult;
import cn.helloworld1999.myweb.entity.Images;
import cn.helloworld1999.myweb.entity.Ratings;
import cn.helloworld1999.myweb.service.ImagesService;
import cn.helloworld1999.myweb.service.RatingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片管理控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/images")
public class ImagesController {
    @Autowired
    private ImagesService imagesService;
    
    @Autowired
    private RatingsService ratingsService;

    /**
     * 获取随机图片
     * @return 随机图片的Base64编码
     */
    @GetMapping("/getRandomImage")
    public ResponseResult<String> getRandomImage() throws IOException {
        log.debug("请求获取随机图片");
        return ResponseResult.success(imagesService.getRandomImage());
    }

    /**
     * 扫描指定目录下的图片并导入到数据库
     * @param path 需要扫描的目录路径
     * @return 新添加的图片数量
     */
    @PostMapping("/scan")
    public ResponseResult<Integer> scanDirectory(
            @RequestParam @NotBlank(message = "扫描路径不能为空") String path) {
        try {
            log.info("开始扫描目录: {}", path);
            int newRecords = imagesService.scanImages(path);
            log.info("扫描完成，新增 {} 张图片", newRecords);
            return ResponseResult.success(newRecords);
        } catch (IOException e) {
            log.error("扫描目录失败: {}, 错误: {}", path, e.getMessage(), e);
            return ResponseResult.error(500, "扫描失败: " + e.getMessage());
        }
    }

    /**
     * 获取带有信息的随机图片
     * @return 图片信息和Base64编码
     */
    @GetMapping("/getRandomImageWithInfo")
    public ResponseResult<ObjectNode> getRandomImageWithInfo() throws IOException {
        log.debug("请求获取带有信息的随机图片");
        Images imageInfo = imagesService.getRandomImageInfo();
        String base64Image = imagesService.convertImageToBase64(imageInfo.getImageUrl());
        Ratings ratings = ratingsService.getRatingsByImageId(imageInfo.getId());
        int score = (ratings != null) ? ratings.getScore() : 0;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("imageInfo", imageInfo);
        json.put("base64Image", base64Image);
        json.put("score", score);
        
        return ResponseResult.success(json);
    }

    /**
     * 获取指定数量的随机图片
     * @param count 要获取的图片数量
     * @return 图片列表及其相关信息
     */
    @GetMapping("/getRandomImagesWithCount")
    public ResponseResult<ObjectNode> getRandomImagesWithCount(
            @RequestParam("count") @Min(value = 1, message = "图片数量必须大于0") int count) throws IOException {
        log.debug("请求获取 {} 张随机图片", count);
        
        // 获取随机图片列表
        List<Images> randomImages = imagesService.getRandomImages(count);
        
        // 创建一个Map来存储每张图片的评分
        Map<Long, Integer> imageRatings = new HashMap<>();
        // 创建一个Map来存储每张图片的Base64编码
        Map<Long, String> imageBase64s = new HashMap<>();

        // 遍历随机图片列表，获取每张图片的评分和Base64编码
        for (Images image : randomImages) {
            Ratings ratings = ratingsService.getRatingsByImageId(image.getId());
            int score = (ratings != null) ? ratings.getScore() : 0;
            imageRatings.put(image.getId(), score);
            String base64Image = imagesService.convertImageToBase64(image.getImageUrl());
            imageBase64s.put(image.getId(), base64Image);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("images", randomImages);
        json.putPOJO("ratings", imageRatings);
        json.putPOJO("base64Images", imageBase64s);
        
        return ResponseResult.success(json);
    }

    /**
     * 根据条件获取随机图片
     * @param minScore 最低评分（可选）
     * @param maxScore 最高评分（可选）
     * @param category 图片分类（可选）
     * @param status 图片状态（可选）
     * @param limit 返回记录数限制（可选）
     * @return 符合条件的图片列表及相关信息
     */
    @GetMapping("/getRandomImageWithConditions")
    public ResponseResult<ObjectNode> getRandomImageWithConditions(
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer limit) throws IOException {
        log.debug("请求获取符合条件的随机图片: minScore={}, maxScore={}, category={}, status={}, limit={}", 
                minScore, maxScore, category, status, limit);

        List<Images> images = imagesService.getRandomImagesWithConditions(minScore, maxScore, category, status, limit);
        
        // 创建一个Map来存储每张图片的评分和Base64编码
        Map<Long, Integer> imageRatings = new HashMap<>();
        Map<Long, String> imageBase64s = new HashMap<>();

        // 遍历图片列表，获取每张图片的评分和Base64编码
        for (Images image : images) {
            Ratings ratings = ratingsService.getRatingsByImageId(image.getId());
            int score = (ratings != null) ? ratings.getScore() : 0;
            imageRatings.put(image.getId(), score);
            String base64Image = imagesService.convertImageToBase64(image.getImageUrl());
            imageBase64s.put(image.getId(), base64Image);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("images", images);
        json.putPOJO("ratings", imageRatings);
        json.putPOJO("base64Images", imageBase64s);
        
        return ResponseResult.success(json);
    }
}