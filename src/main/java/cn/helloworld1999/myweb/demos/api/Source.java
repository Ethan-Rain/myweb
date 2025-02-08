package cn.helloworld1999.myweb.demos.api;

import cn.helloworld1999.myweb.demos.api.model.Image;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import cn.helloworld1999.myweb.mapper.ImageMapper;
import cn.helloworld1999.myweb.util.ImageImporter;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
@RestController
@RequestMapping("/api/source")
public class Source {

    private static final String IMAGE_PATH = "Z:\\2.涩涩\\恢复\\2024-10-19 22.11.39\\图片\\jpg\\folder_0";

    @Autowired
    private ImageImporter imageImporter;

    @Autowired
    private ImageMapper imageMapper;

    @RequestMapping("/getImageRandomTest")
    public ResponseEntity<Resource> getImage() throws IOException {
        // 调试输出
        System.out.println("\n===== 请求开始 =====");

        // 直接从数据库中随机获取一条图片路径
        Image randomImage = imageMapper.selectRandomImage();
        if (randomImage == null) {
            System.out.println("错误：随机获取的图片不存在");
            return ResponseEntity.notFound().build();
        }

        System.out.println("随机选中图片: " + randomImage.getName());
        System.out.println("图片路径: " + randomImage.getPath());

        Path imagePath = Paths.get(randomImage.getPath());
        Resource imageResource = new FileSystemResource(imagePath.toFile());

        if (!imageResource.exists()) {
            System.out.println("错误：文件不存在");
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(getContentType(randomImage.getName())));

        System.out.println("===== 请求成功 =====");
        return ResponseEntity.ok().headers(headers).body(imageResource);
    }

    @RequestMapping("/importImages")
    public Map<String, Object> importImages(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String directoryPath = request.get("directoryPath");
        
        // 检查目录路径是否为空
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "目录路径不能为空");
            return response;
        }

        // 检查目录是否存在
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            response.put("success", false);
            response.put("message", "目录不存在或不是有效目录");
            return response;
        }

        try {
            // 执行导入
            imageImporter.importImagesFromDirectory(directoryPath);
            
            // 获取导入后的图片总数
            int totalImages = getTotalImages();
            
            response.put("success", true);
            response.put("totalImages", totalImages);
            response.put("message", "图片导入成功");
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "图片导入失败：" + e.getMessage());
            log.error("图片导入失败", e);
        }
        
        return response;
    }

    private int getTotalImages() {
        return imageMapper.countAllImages();
    }

    private String getContentType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (lowerName.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
