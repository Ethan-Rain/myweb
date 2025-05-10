package cn.helloworld1999.webcrawler.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface DownloaderStrategy {
    Boolean start(JsonNode jsonNode) throws IOException;
}
