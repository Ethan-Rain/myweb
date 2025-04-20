package cn.helloworld1999.myweb.controller;

import cn.helloworld1999.myweb.dto.ResponseResult;
import cn.helloworld1999.myweb.entity.Ratings;
import cn.helloworld1999.myweb.service.RatingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Map;

/**
 * 评分服务控制器
 * 处理图片评分相关的请求
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ratings")
public class RatingsServiceController {
    /**
     * 评分服务依赖注入
     */
    @Autowired
    private RatingsService ratingsService;

    /**
     * 获取所有评分记录
     * @return 评分记录列表
     */
    @GetMapping("/all")
    public ResponseResult<ArrayList<Ratings>> getAllRatings() {
        log.debug("请求获取所有评分记录");
        return ResponseResult.success(ratingsService.getAllRatings());
    }

    /**
     * 根据图片ID获取评分
     * @param imageId 图片ID
     * @return 评分记录
     */
    @GetMapping("/image/{imageId}")
    public ResponseResult<Ratings> getRatingsByImageId(
            @PathVariable @NotNull(message = "图片ID不能为空") Long imageId) {
        log.debug("请求获取图片ID为{}的评分", imageId);
        Ratings rating = ratingsService.getRatingsByImageId(imageId);
        return ResponseResult.success(rating);
    }

    /**
     * 更新图片评分
     * @param imageId 图片ID
     * @param score 新的评分
     * @return 更新结果，成功返回true，失败返回错误信息
     */
    @PutMapping("/change/{imageId}/{score}")
    public ResponseResult<Boolean> changeRatings(
            @PathVariable("imageId") @NotNull(message = "图片ID不能为空") Long imageId,
            @PathVariable("score") @Min(value = 1, message = "评分最小值为1") 
                                  @Max(value = 5, message = "评分最大值为5") Integer score) {
        log.debug("请求更新图片ID为{}的评分为{}", imageId, score);
        if (ratingsService.changeRatings(imageId, score)) {
            return ResponseResult.success(true);
        }
        return ResponseResult.error(400, "评分更新失败");
    }

    /**
     * 根据评分范围和排序方式筛选评分
     * @param minScore 最低评分
     * @param maxScore 最高评分
     * @param order 排序方式
     * @return 符合条件的评分列表
     */
    @GetMapping("/filter/{minScore}/{maxScore}/{order}")
    public ResponseResult<ArrayList<Ratings>> filterRatingsByScore(
            @PathVariable("minScore") @Min(value = 0, message = "最低评分不能小于0") Integer minScore,
            @PathVariable("maxScore") @Min(value = 1, message = "最高评分不能小于1") 
                                     @Max(value = 5, message = "最高评分不能大于5") Integer maxScore,
            @PathVariable("order") @Pattern(regexp = "asc|desc", 
                                         message = "排序方式只能是asc或desc") String order) {
        log.debug("请求按评分范围[{}-{}]和排序[{}]筛选评分", minScore, maxScore, order);
        return ResponseResult.success(
            ratingsService.filterRatingsByScoresAndOrder(minScore, maxScore, order)
        );
    }

    /**
     * 统计各评分的数量
     * @return 评分统计结果，key为评分值，value为该评分的数量
     */
    @GetMapping("/filter/countRatingsByScore")
    public ResponseResult<Map<Integer, Integer>> countRatingsByScore() {
        log.debug("请求统计各评分数量");
        return ResponseResult.success(ratingsService.countRatingsByScore());
    }
}