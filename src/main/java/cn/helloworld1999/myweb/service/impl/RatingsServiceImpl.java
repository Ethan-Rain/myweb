package cn.helloworld1999.myweb.service.impl;

import cn.helloworld1999.myweb.entity.Ratings;
import cn.helloworld1999.myweb.mapper.RatingsMapper;
import cn.helloworld1999.myweb.service.RatingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class RatingsServiceImpl implements RatingsService {
    @Autowired
    private RatingsMapper ratingsMapper;
    @Autowired
    private DefaultErrorAttributes errorAttributes;

    @Override
    public ArrayList<Ratings> getAllRatings() {
        return ratingsMapper.getAllRatings();
    }

    @Override
    public Ratings getRatingsByImageId(Long imageId) {
        if (ratingsMapper.getRatingsByImageId(imageId) != null) {
            return ratingsMapper.getRatingsByImageId(imageId);
        }
        return null;
    }

    @Override
    public Boolean changeRatings(Long imageId, Integer score) {
        if (ratingsMapper.getRatingsByImageId(imageId) != null) {
            return ratingsMapper.changeRatings(imageId, score) == 1;
        }

        return ratingsMapper.addRatings(imageId, score) == 1;

    }

    @Override
    public ArrayList<Ratings> filterRatingsByScore(Integer minScore, Integer maxScore, String order) {
        return ratingsMapper.filterRatingsByScoresAndOrder(minScore, maxScore, order);
    }

    @Override
    public ArrayList<Ratings> filterRatingsByScoresAndOrder(Integer minScore, Integer maxScore, String order) {
        minScore = minScore == null ? 0 : minScore;
        maxScore = maxScore == null ? 5 : maxScore;
        return ratingsMapper.filterRatingsByScoresAndOrder(minScore, maxScore, order);
    }

    @Override
    public Map<Integer, Integer> countRatingsByScore() {
        return ratingsMapper.countRatingsByScore();
    }

}