package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.dto.ScanResultDTO;
import cn.helloworld1999.mediaservice.service.FileScanner;
import jdk.dynalink.beans.StaticClass;
import org.springframework.beans.factory.annotation.Value;

public class FileScannerV3Impl implements FileScanner {
    @Value("${custom-configuration.enable-redis}")
    static Boolean enableRedis;
    @Override
    public ScanResultDTO scanDirectory(String path, Long category, boolean useRedis) {
        useRedis=enableRedis;


        return null;
    }
}
