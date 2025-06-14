package cn.helloworld1999.job;

import cn.helloworld1999.mediaservice.service.CacheService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CacheSyncJob {

    private static final Logger logger = LoggerFactory.getLogger(CacheSyncJob.class);

    @Autowired
    private CacheService cacheService;

    @XxlJob("cacheSyncJob")
    public ReturnT<String> execute(String param) {
        try {
            logger.info("缓存同步定时任务开始，参数：{}", param);
            Map<String, Object> result = cacheService.syncToRedis();
            boolean success = Boolean.TRUE.equals(result.get("success"));
            logger.info("缓存同步定时任务完成，结果：{}", success ? "成功" : "失败");
            return success ? ReturnT.SUCCESS : ReturnT.FAIL;
        } catch (Exception e) {
            logger.error("缓存同步定时任务执行失败", e);
            return new ReturnT<>(500, e.getMessage());
        }
    }
}