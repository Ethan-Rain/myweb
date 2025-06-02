package cn.helloworld1999.mediaservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConfigurationProperties(prefix = "file")
@Configuration
public class FileConfig {
    private String baseDir;
    private String baseUrl;

    /**
     * 验证路径是否在基础目录下
     * @param path 相对路径
     * @return 是否安全
     */
    public boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        
        // 解码URL编码的路径
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            return false;
        }
        
        // 构建完整路径
        String fullPath = baseDir + path;
        File file = new File(fullPath);
        
        // 检查是否在基础目录下
        File baseDirFile = new File(baseDir);
        try {
            File canonicalFile = file.getCanonicalFile();
            File canonicalBaseDir = baseDirFile.getCanonicalFile();
            return canonicalFile.getAbsolutePath().startsWith(canonicalBaseDir.getAbsolutePath());
        } catch (Exception e) {
            return false;
        }
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
