package cn.helloworld1999.mediaservice.service;

import cn.helloworld1999.mediaservice.dto.ScanResultDTO;
import org.springframework.stereotype.Service;

@Service
public interface FileScanner {
    ScanResultDTO scanDirectory(String path, Long category, boolean useRedis);
}
