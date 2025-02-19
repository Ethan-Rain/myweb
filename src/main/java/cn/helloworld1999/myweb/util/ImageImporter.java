package cn.helloworld1999.myweb.util;

import cn.helloworld1999.myweb.demos.api.model.Image;
import cn.helloworld1999.myweb.demos.api.model.ImageMetadata;
import cn.helloworld1999.myweb.mapper.ImageMapper;
import cn.helloworld1999.myweb.mapper.ImageMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ImageImporter {

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ImageMetadataMapper imageMetadataMapper;

    /**
     * 从指定目录导入图片
     *
     * @param directoryPath 图片目录路径
     * @throws IOException 如果读取文件时发生错误
     */
    public void importImagesFromDirectory(String directoryPath) throws IOException {
        Path imageDirPath = Paths.get(directoryPath);
        File imageDir = imageDirPath.toFile();

        if (!imageDir.exists() || !imageDir.isDirectory()) {
            throw new IllegalArgumentException("路径不存在或不是目录");
        }

        List<File> imageFiles = Files.list(imageDirPath)
                .filter(path -> {
                    String fileName = path.getFileName().toString().toLowerCase();
                    return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png");
                })
                .map(Path::toFile)
                .collect(Collectors.toList());

        for (File imageFile : imageFiles) {
            Image image = createImageFromFile(imageFile);
            imageMapper.insert(image);

            ImageMetadata metadata = createImageMetadataFromFile(imageFile, image.getId());
            imageMetadataMapper.insert(metadata);
        }
    }

    /**
     * 从文件创建Image对象
     *
     * @param imageFile 图片文件
     * @return Image对象
     * @throws IOException 如果读取文件时发生错误
     */
    private Image createImageFromFile(File imageFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        if (bufferedImage == null) {
            throw new RuntimeException("无法读取图片文件: " + imageFile.getPath());
        }

        Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setName(imageFile.getName());
        image.setPath(imageFile.getAbsolutePath());
        image.setContentType(Files.probeContentType(imageFile.toPath()));
        image.setSize(imageFile.length());
        image.setWidth(bufferedImage.getWidth());
        image.setHeight(bufferedImage.getHeight());

        return image;
    }

    /**
     * 从文件创建ImageMetadata对象
     *
     * @param imageFile 图片文件
     * @param imageId 图片ID
     * @return ImageMetadata对象
     */
    private ImageMetadata createImageMetadataFromFile(File imageFile, String imageId) {
        ImageMetadata metadata = new ImageMetadata();
        metadata.setImageId(imageId);

        return metadata;
    }
}