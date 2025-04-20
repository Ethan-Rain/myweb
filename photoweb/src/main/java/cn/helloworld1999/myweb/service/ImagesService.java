package cn.helloworld1999.myweb.service;

import cn.helloworld1999.myweb.entity.Images;

import java.io.IOException;
import java.util.List;

public interface ImagesService {
    // 利用照片id获取一张照片，以Base64形式返回一张真正的照片
    Images getImage(Long id);
    // 获取照片的Base64形式
    String convertImageToBase64(String filePath) throws IOException;
    // 获取照片信息
    Images getImageInfo(Long id);
    // 随机获取一张Base64照片
    String getRandomImage() throws IOException;
    // 扫描指定路径下的图片，并将信息存入数据库中,返回扫描到的图片数量
    Integer scanImages(String path) throws IOException;
    // 随机获取一条图片信息
    Images getRandomImageInfo() throws IOException;

    List<Images> getRandomImages(int count);

    List<Images> getRandomImagesWithConditions(Integer minScore, Integer maxScore, String category, String status, Integer limit);

}
