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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/images")
public class ImagesController {
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private RatingsService ratingsService;

    // 获取随机图片,且不携带任何信息
    @RequestMapping("/getRandomImage")
    public ResponseResult<String> getRandomImage() throws Exception {
        if (imagesService.getRandomImage() == null) {
            return ResponseResult.error(500, "获取图片失败");
        } else {
            return ResponseResult.success(imagesService.getRandomImage());
        }
    }

    @PostMapping("/scan")
    public ResponseResult<Integer> scanDirectory(
            @RequestParam @NotBlank String path) {
        try {
            int newRecords = imagesService.scanImages(path);
            return ResponseResult.success(newRecords);
        } catch (IOException e) {
            return ResponseResult.error(500, "扫描失败: " + e.getMessage());
        }
    }

    @GetMapping("/getRandomImageWithInfo")
    public ResponseResult<Object> getRandomImageWithInfo() throws Exception {
        Images imageInfo = imagesService.getRandomImageInfo();
        String base64Image = imagesService.convertImageToBase64(imageInfo.getImageUrl());
        Ratings ratings = ratingsService.getRatingsByImageId(imageInfo.getId());
        int score = (ratings != null) ? ratings.getScore() : 0;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("imageInfo", imageInfo);  // 处理实体类序列化
        json.put("base64Image", base64Image);
        json.put("score", score);
        log.debug(json.toString());
        return ResponseResult.success(json);
    }

    @GetMapping("/getRandomImagesWithCount")
    public ResponseResult<Object> getRandomImagesWithCount(@RequestParam("count") int count) throws IOException {
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
        log.debug(json.toString());
        return ResponseResult.success(json);
    }

    @GetMapping("/getRandomImageWithConditions")
    public ResponseResult<Object> getRandomImageWithConditions(
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer limit) throws IOException {

    List<Images> images = imagesService.getRandomImagesWithConditions(minScore, maxScore, category, status,limit);
    
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
    log.debug(json.toString());
    return ResponseResult.success(json);
}

}