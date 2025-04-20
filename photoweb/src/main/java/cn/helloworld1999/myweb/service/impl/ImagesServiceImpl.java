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

    /**
     * 图片服务实现类
     * 处理所有与图片相关的业务逻辑
     */
    @Service
    public class ImagesServiceImpl implements ImagesService {
        /**
         * 图片数据访问对象
         */
        @Autowired
        private ImagesMapper imagesMapper;

        /**
         * 根据ID获取单张图片信息
         * @param id 图片ID
         * @return 图片实体对象
         */
        @Override
        public Images getImage(Long id) {
            return imagesMapper.selectByPrimaryKey(id);
        }

        /**
         * 将图片文件转换为Base64编码字符串
         * @param filePath 图片文件路径
         * @return Base64编码的字符串
         * @throws IOException 文件读取异常
         * @throws IllegalArgumentException 文件路径无效时抛出
         */
        public String convertImageToBase64(String filePath) throws IOException {
            if (filePath == null || filePath.trim().isEmpty()) {
                throw new IllegalArgumentException("文件路径不能为空");
            }
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            return Base64.getEncoder().encodeToString(fileContent);
        }

        /**
         * 获取图片详细信息
         * @param id 图片ID
         * @return 图片实体对象
         */
        @Override
        public Images getImageInfo(Long id) {
            return null;
        }

        /**
         * 获取随机图片的Base64编码
         * @return Base64编码的图片字符串
         * @throws IOException 文件读取异常
         */
        @Override
        public String getRandomImage() throws IOException {
            return convertImageToBase64(imagesMapper.selectRandomImage().getImageUrl());
        }

        /**
         * 支持的图片文件扩展名集合
         */
        private static final Set<String> IMAGE_EXTENSIONS = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"))
        );

        /**
         * 扫描指定目录下的图片文件并添加到数据库
         * @param directoryPath 需要扫描的目录路径
         * @return 新增的图片数量
         * @throws IOException 目录访问异常
         * @throws IllegalArgumentException 目录路径无效时抛出
         */
        @Override
        public Integer scanImages(String directoryPath) throws IOException {
            Path root = Paths.get(directoryPath);
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

        /**
         * 获取随机图片信息
         * @return 随机图片的实体对象
         * @throws IOException 数据访问异常
         */
        @Override
        public Images getRandomImageInfo() throws IOException {
            return imagesMapper.selectRandomImage();
        }

        /**
         * 获取指定数量的随机图片
         * @param count 需要获取的图片数量
         * @return 图片实体对象列表
         */
        @Override
        public List<Images> getRandomImages(int count) {
            return imagesMapper.getRandomImages(count);
        }

        /**
         * 根据条件获取随机图片
         * @param minScore 最低评分
         * @param maxScore 最高评分
         * @param category 图片分类
         * @param status 图片状态
         * @param limit 返回数量限制
         * @return 符合条件的图片列表
         */
        @Override
        public List<Images> getRandomImagesWithConditions(Integer minScore, Integer maxScore, String category, String status, Integer limit) {
            return imagesMapper.getRandomImagesWithConditions(minScore, maxScore, category, status, limit);
        }

        /**
         * 检查文件是否为有效的图片文件
         * @param file 待检查的文件路径
         * @return 如果是支持的图片文件返回true，否则返回false
         */
        private boolean isValidImage(Path file) {
            if (Files.isDirectory(file)) return false;
            String fileName = file.getFileName().toString().toLowerCase();
            return IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
        }

        /**
         * 处理单个图片文件
         * @param file 图片文件路径
         * @param counter 计数器，用于统计处理的图片数量
         * @throws IOException 文件处理异常
         */
        private void processImage(Path file, AtomicInteger counter) throws IOException {
            String filePath = file.toAbsolutePath().toString();
            if (imagesMapper.existsByImageUrl(filePath) == null) {
                Images image = new Images()
                        .setImageUrl(filePath)
                        .setUserId(1L)
                        .setFileName(file.getFileName().toString())
                        .setSize(Files.size(file))
                        .setFormat(parseFileExtension(file))
                        .setStatus("pending")
                        .setCreatedAt(new Date())
                        .setUpdatedAt(new Date());
                imagesMapper.insertSelective(image);
                counter.incrementAndGet();
            }
        }

        /**
         * 解析文件扩展名
         * @param file 文件路径
         * @return 文件扩展名（不包含点号）
         */
        private String parseFileExtension(Path file) {
            String name = file.getFileName().toString();
            int dotIndex = name.lastIndexOf('.');
            return (dotIndex > 0) ? name.substring(dotIndex + 1) : "";
        }
    }