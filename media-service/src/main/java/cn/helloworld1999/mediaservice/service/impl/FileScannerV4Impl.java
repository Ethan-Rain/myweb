package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.dto.ScanResultDTO;
import cn.helloworld1999.mediaservice.entity.FileInfo;
import cn.helloworld1999.mediaservice.mapper.FileInfoMapper;
import cn.helloworld1999.mediaservice.service.FileScanner;
import cn.helloworld1999.mediaservice.service.SynologyFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("fileScannerV4")
public class FileScannerV4Impl implements FileScanner {

    @Autowired
    private SynologyFileService synologyFileService;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_FILE_INFO_KEY = "file:info:";
    private static final int REDIS_EXPIRE_DAYS = 7;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScanResultDTO scanDirectory(String path, Long category, boolean useRedis) {
        String cacheKey = REDIS_FILE_INFO_KEY + DigestUtils.md5DigestAsHex(path.getBytes());

        // 如果启用Redis并且缓存中存在数据，直接返回
        if (useRedis) {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                log.info("Hit cache for path: {}", path);
                return (ScanResultDTO) cachedResult;
            }
        }

        // 使用群晖API获取文件列表
        List<Map<String, Object>> files = synologyFileService.listFiles(path, true);
        List<FileInfo> fileInfoList = new ArrayList<>();
        List<Map<String, Object>> storedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        ScanResultDTO.ScanStats stats = new ScanResultDTO.ScanStats();
        long totalSize = 0;

        for (Map<String, Object> file : files) {
            try {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setPath((String) file.get("path"));
                fileInfo.setName((String) file.get("name"));
                fileInfo.setSize((Long) file.get("size"));

                Map<String, Object> time = (Map<String, Object>) file.get("time");
                fileInfo.setCreateTime(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochSecond((Long) time.get("ctime")),
                    ZoneId.systemDefault()));
                fileInfo.setModifyTime(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochSecond((Long) time.get("mtime")),
                    ZoneId.systemDefault()));

                fileInfo.setIsDirectory((Boolean) file.get("isdir"));
                fileInfo.setCategory(category);
                fileInfo.setScanTime(LocalDateTime.now());

                // 转换为存储格式
                Map<String, Object> storedFile = new HashMap<>(file);
                storedFile.put("category", category);
                storedFile.put("scanTime", fileInfo.getScanTime());

                // 更新文件信息到数据库
                try {
                    // 检查是否已存在
                    FileInfo existing = fileInfoMapper.selectOne(
                        new LambdaQueryWrapper<FileInfo>()
                            .eq(FileInfo::getPath, fileInfo.getPath())
                    );

                    if (existing != null) {
                        fileInfo.setId(existing.getId());
                        fileInfoMapper.updateById(fileInfo);
                        stats.setSkippedCount(stats.getSkippedCount() + 1);
                    } else {
                        fileInfoMapper.insert(fileInfo);
                        stats.setSuccessCount(stats.getSuccessCount() + 1);
                    }

                    fileInfoList.add(fileInfo);
                    storedFiles.add(storedFile);
                    totalSize += fileInfo.getSize() != null ? fileInfo.getSize() : 0;
                    stats.setTotalFiles(stats.getTotalFiles() + 1);
                } catch (Exception e) {
                    log.error("Error processing file: " + fileInfo.getPath(), e);
                    failedFiles.add(fileInfo.getPath());
                    stats.setFailureCount(stats.getFailureCount() + 1);
                }
            } catch (Exception e) {
                log.error("Error converting file data: " + file, e);
                failedFiles.add(String.valueOf(file.get("path")));
                stats.setFailureCount(stats.getFailureCount() + 1);
            }
        }

        ScanResultDTO result = new ScanResultDTO()
            .setStatus("SUCCESS")
            .setStats(stats)
            .setFailedFiles(failedFiles)
            .setStoredFiles(storedFiles)
            .setFileList(fileInfoList)
            .setTotalSize(totalSize);

        // 如果有失败的文件，设置状态为部分成功
        if (!failedFiles.isEmpty()) {
            result.setStatus("PARTIAL_SUCCESS");
            result.setErrorMessage("Some files failed to process. Check failedFiles for details.");
        }

        // 如果启用Redis，将结果存入缓存
        if (useRedis) {
            redisTemplate.opsForValue().set(cacheKey, result, REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
        }

        return result;
    }
}
