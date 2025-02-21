package cn.helloworld1999.myweb.service.impl;

import cn.helloworld1999.myweb.entity.Images;
import cn.helloworld1999.myweb.mapper.ImagesMapper;
import cn.helloworld1999.myweb.service.ImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ImagesServiceImpl implements ImagesService {
    @Autowired
    private ImagesMapper imagesMapper;

    @Override
    public Images getImage(Long id) {
        // 从数据库中一条照片信息
        return imagesMapper.selectByPrimaryKey(id);
    }

    public String convertImageToBase64(String filePath) throws IOException {
        // 参数有效性检查
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        // 通过NIO方式读取文件内容
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        // 使用Java8内置Base64编码器进行编码
        return Base64.getEncoder().encodeToString(fileContent);
    }

    @Override
    public Images getImageInfo(Long id) {
        return null;
    }

    @Override
    public String getRandomImage() throws IOException {
        return convertImageToBase64(imagesMapper.selectRandomImage().getImageUrl());
    }


    private static final Set<String> IMAGE_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"))
    );

    @Override
    public Integer scanImages(String directoryPath) throws IOException {
        Path root = Paths.get(directoryPath);

        // 参数有效性验证
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("无效的目录路径: " + directoryPath);
        }

        AtomicInteger counter = new AtomicInteger();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    if (isValidImage(file)) {
                        processImage(file, counter);
                    }
                } catch (Exception e) {
                    System.err.printf("处理文件 %s 失败: %s%n", file, e.getMessage());
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return counter.get();
    }

    private boolean isValidImage(Path file) {
        if (Files.isDirectory(file)) return false;

        String fileName = file.getFileName().toString().toLowerCase();
        return IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    private void processImage(Path file, AtomicInteger counter) throws IOException {
        String filePath = file.toAbsolutePath().toString();

        // 数据库存在性检查
        if (imagesMapper.existsByImageUrl(filePath) == null) {
            Images image = new Images()
                    .setImageUrl(filePath)
                    .setUserId(1L)
                    .setFileName(file.getFileName().toString())
                    .setSize(Files.size(file))
                    .setFormat(parseFileExtension(file))
                    .setStatus("pending")  // 默认待审核状态
                    .setCreatedAt(new Date())
                    .setUpdatedAt(new Date());

            imagesMapper.insertSelective(image);
            counter.incrementAndGet();
        }
    }

    private String parseFileExtension(Path file) {
        String name = file.getFileName().toString();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex > 0) ? name.substring(dotIndex + 1) : "";
    }

}
