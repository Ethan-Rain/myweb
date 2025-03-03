package cn.helloworld1999.myweb.controller;

        import cn.helloworld1999.myweb.dto.ResponseResult;
        import cn.helloworld1999.myweb.entity.Ratings;
        import cn.helloworld1999.myweb.service.RatingsService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;
        import java.util.Map;

        /**
         * 评分服务控制器
         * 处理所有与图片评分相关的HTTP请求
         */
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
            public ArrayList<Ratings> getAllRatings() {
                return ratingsService.getAllRatings();
            }

            /**
             * 根据图片ID获取评分
             * @param imageId 图片ID
             * @return 评分记录
             */
            @GetMapping("/image/{imageId}")
            public Ratings getRatingsByImageId(@PathVariable Long imageId) {
                return ratingsService.getRatingsByImageId(imageId);
            }

            /**
             * 更新图片评分
             * @param imageId 图片ID
             * @param score 新的评分
             * @return 更新结果，成功返回true，失败返回错误信息
             */
            @PutMapping("/change/{imageId}/{score}")
            public ResponseResult<Boolean> changeRatings(@PathVariable("imageId") Long imageId,
                                                       @PathVariable("score") Integer score) {
                if (ratingsService.changeRatings(imageId, score))
                    return ResponseResult.success(true);
                return ResponseResult.error(400, "Failed to change ratings");
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
                    @PathVariable("minScore") Integer minScore,
                    @PathVariable("maxScore") Integer maxScore,
                    @PathVariable("order") String order) {
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
                return ResponseResult.success(ratingsService.countRatingsByScore());
            }
        }