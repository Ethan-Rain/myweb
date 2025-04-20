package cn.helloworld1999.myweb.util;

import cn.helloworld1999.myweb.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 图片处理工具类
 */
@Slf4j
@Component
public class ImageUtils {

    /**
     * 支持的图片文件扩展名集合
     */
    private static final Set<String> IMAGE_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"))
    );

    /**
     * 将图片文件转换为Base64编码字符串
     * @param filePath 图片文件路径
     * @return Base64编码的字符串
     * @throws IOException 文件读取异常
     * @throws BusinessException 文件路径无效时抛出
     */
    public String convertImageToBase64(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new BusinessException("文件路径不能为空");
        }
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new BusinessException(404, "文件不存在: " + filePath);
        }
        
        if (!isValidImage(path)) {
            throw new BusinessException("不支持的图片格式");
        }
        
        byte[] fileContent = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * 检查文件是否为有效的图片文件
     * @param file 待检查的文件路径
     * @return 如果是支持的图片文件返回true，否则返回false
     */
    public boolean isValidImage(Path file) {
        if (Files.isDirectory(file)) return false;
        
        String fileName = file.getFileName().toString().toLowerCase();
        return IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    /**
     * 解析文件扩展名
     * @param file 文件路径
     * @return 文件扩展名（不包含点号）
     */
    public String parseFileExtension(Path file) {
        String name = file.getFileName().toString();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex > 0) ? name.substring(dotIndex + 1) : "";
    }
    
    /**
     * 获取图片的分辨率
     * @param filePath 图片文件路径
     * @return 图片分辨率字符串，例如"1920x1080"
     * @throws IOException 文件读取异常
     */
    public String getImageResolution(String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        if (image == null) {
            return "unknown";
        }
        return image.getWidth() + "x" + image.getHeight();
    }
}
