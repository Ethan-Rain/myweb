package cn.helloworld1999.mediaservice.controller;

import cn.helloworld1999.mediaservice.dto.ScanResultDTO;
import cn.helloworld1999.mediaservice.service.impl.FileScannerImpl;
import cn.helloworld1999.mediaservice.service.impl.FileScannerV2Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件扫描控制器
 */
@RestController
@RequestMapping("/scan")
public class FileScannerController {
    @Autowired
    FileScannerImpl fileScanner;
    @Autowired
    FileScannerV2Impl fileScannerV2;
    /**
     * 扫描指定目录
     * @param path 目录路径
     * @param category 分类ID
     * @param useRedis 是否使用Redis存储
     * @return 扫描结果
     */
    @PostMapping("/directory")
    public ScanResultDTO scanDirectory(
            @RequestParam String path,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false, defaultValue = "false") boolean useRedis) {
        return fileScanner.scanDirectory(path, category, useRedis);
    }
    @PostMapping("/directory/v2")
    public ScanResultDTO scanDirectoryV2(
            @RequestParam String path,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false, defaultValue = "false") boolean useRedis) {
        return fileScannerV2.scanDirectory(path, category, useRedis);
    }
}
