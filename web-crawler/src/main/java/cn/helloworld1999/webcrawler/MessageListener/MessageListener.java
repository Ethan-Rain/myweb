package cn.helloworld1999.webcrawler.MessageListener;

import cn.helloworld1999.webcrawler.enums.DownloadStrategyEnum;
import cn.helloworld1999.webcrawler.strategy.DownloaderStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;

@Component
public class MessageListener {
    @Autowired
    Map<String, DownloaderStrategy> downloaderStrategyMap;
    // 记录收到的下载任务队列
    static Queue<String> faildDownloadTaskQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "fapello_download")
    public void processMessage(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            Integer platform = Integer.valueOf(jsonNode.get("platform").asText());
            logger.info("接收到消息：{}", jsonNode);
            if (downloaderStrategyMap.containsKey(DownloadStrategyEnum.fromCode(platform).getDescription())) {
                Boolean result = downloaderStrategyMap.get(DownloadStrategyEnum.fromCode(platform).getDescription()).start(jsonNode);
                if (!result) {
                    logger.error("下载失败，添加到失败队列 {}", message);
                    faildDownloadTaskQueue.add(message);
                }
            } else {
                logger.error("未知的平台：{}", platform);
            }
        } catch (Exception e) {
            logger.error("处理消息时发生错误", e);
        }
    }
}