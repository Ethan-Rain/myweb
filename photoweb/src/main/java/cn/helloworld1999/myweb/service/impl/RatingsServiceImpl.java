package cn.helloworld1999.myweb.service.impl;

import cn.helloworld1999.myweb.entity.Ratings;
import cn.helloworld1999.myweb.exception.BusinessException;
import cn.helloworld1999.myweb.mapper.RatingsMapper;
import cn.helloworld1999.myweb.service.RatingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

/**
 * 评分服务实现类
 */
@Slf4j
@Service
public class RatingsServiceImpl implements RatingsService {
    @Autowired
    private RatingsMapper ratingsMapper;

    /**
     * 获取所有评分记录
     * @return 评分记录列表
     */
    @Override
    public ArrayList<Ratings> getAllRatings() {
        log.debug("获取所有评分记录");
        return ratingsMapper.getAllRatings();
    }

    /**
     * 根据图片ID获取评分
     * @param imageId 图片ID
     * @return 评分记录，可能为null
     */
    @Override
    public Ratings getRatingsByImageId(Long imageId) {
        if (imageId == null) {
            throw new BusinessException("图片ID不能为空");
        }
        log.debug("获取图片ID：{}的评分", imageId);
        return ratingsMapper.getRatingsByImageId(imageId);
    }

    /**
     * 修改或新增图片评分
     * @param imageId 图片ID
     * @param score 评分分数
     * @return 操作是否成功
     */
    @Override
    @Transactional
    public Boolean changeRatings(Long imageId, Integer score) {
        if (imageId == null) {
            throw new BusinessException("图片ID不能为空");
        }
        
        if (score == null || score < 1 || score > 5) {
            throw new BusinessException("评分必须在1到5之间");
        }
        
        Ratings existingRating = ratingsMapper.getRatingsByImageId(imageId);
        try {
            if (existingRating != null) {
                log.debug("更新图片ID：{}的评分为{}", imageId, score);
                return ratingsMapper.changeRatings(imageId, score) == 1;
            } else {
                log.debug("为图片ID：{}添加新评分{}", imageId, score);
                return ratingsMapper.addRatings(imageId, score) == 1;
            }
        } catch (Exception e) {
            log.error("修改评分失败：imageId={}, score={}, 错误：{}", imageId, score, e.getMessage(), e);
            throw new BusinessException("评分操作失败：" + e.getMessage());
        }
    }

    /**
     * 根据评分范围和排序方式筛选评分
     * @param minScore 最低评分
     * @param maxScore 最高评分
     * @param order 排序方式
     * @return 符合条件的评分列表
     */
    @Override
    public ArrayList<Ratings> filterRatingsByScore(Integer minScore, Integer maxScore, String order) {
        return filterRatingsByScoresAndOrder(minScore, maxScore, order);
    }

    /**
     * 根据评分范围和排序方式筛选评分（增强版）
     * @param minScore 最低评分，默认为0
     * @param maxScore 最高评分，默认为5
     * @param order 排序方式，默认为asc
     * @return 符合条件的评分列表
     */
    @Override
    public ArrayList<Ratings> filterRatingsByScoresAndOrder(Integer minScore, Integer maxScore, String order) {
        // 参数验证和默认值设置
        minScore = (minScore == null || minScore < 0) ? 0 : minScore;
        maxScore = (maxScore == null || maxScore > 5) ? 5 : maxScore;
        
        if (minScore > maxScore) {
            throw new BusinessException("最低评分不能高于最高评分");
        }
        
        // 验证排序参数
        if (order != null && !order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            log.warn("无效的排序参数：{}，使用默认值asc", order);
            order = "asc";
        }
        
        log.debug("按评分范围[{}-{}]和排序[{}]筛选评分", minScore, maxScore, order);
        return ratingsMapper.filterRatingsByScoresAndOrder(minScore, maxScore, order);
    }

    /**
     * 统计各评分的数量
     * @return 评分统计结果，key为评分值，value为该评分的数量
     */
    @Override
    public Map<Integer, Integer> countRatingsByScore() {
        log.debug("统计各评分数量");
        return ratingsMapper.countRatingsByScore();
    }
}