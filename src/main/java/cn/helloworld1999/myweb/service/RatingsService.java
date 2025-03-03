package cn.helloworld1999.myweb.service;

import cn.helloworld1999.myweb.entity.Ratings;

import java.util.ArrayList;
import java.util.Map;

public interface RatingsService {
    // 获取所有评分
    ArrayList<Ratings> getAllRatings();
    // 获取指定图片的评分
    Ratings getRatingsByImageId(Long imageId);
    // 改变指定图片的评分
    Boolean changeRatings(Long imageId,Integer score);

    ArrayList<Ratings> filterRatingsByScore(Integer minScore, Integer maxScore, String order);
    // 统计所有评分及其数量
    ArrayList<Ratings> filterRatingsByScoresAndOrder(Integer minScore, Integer maxScore, String order);

    Map<Integer, Integer> countRatingsByScore();
}
