package cn.helloworld1999.webcrawler.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.UUID;

import static cn.helloworld1999.webcrawler.strategy.FapelloDownloader.PROXY_HOST;
import static cn.helloworld1999.webcrawler.strategy.FapelloDownloader.PROXY_PORT;

@Component("Cosplaytele")
public class CosplayteleDownloader implements DownloaderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CosplayteleDownloader.class);

    private String URL;
    private Path SAVE_DIR; // 文件保存目录
    private JsonNode JSON_NODE;

    @Override
    public Boolean start(JsonNode jsonNode) throws IOException {
        JSON_NODE = jsonNode;
        URL = jsonNode.has("url") ? jsonNode.get("url").asText(URL) : URL;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        Document document = Jsoup.connect(URL).proxy(proxy)  // 设置代理
                .userAgent("Mozilla/5.0")
                .timeout(30000).get();
        String downloadUrl = extractDownloadUrl(document);
        if (downloadUrl != null) {
            logger.info("成功提取下载链接: {}", downloadUrl);
            // 初始化保存目录
            SAVE_DIR = Paths.get("\\\\192.168.31.103\\存储空间\\2.2.爬虫").resolve(UUID.randomUUID().toString());
            Files.createDirectories(SAVE_DIR);
            return this.downLoadByURL(downloadUrl);
        } else {
            logger.warn("未找到有效的下载链接");
            return false;
        }
    }

    /**
     * 提取绑定在按钮上的下载链接
     *
     * @param document HTML文档
     * @return 下载链接，若未找到则返回null
     */
    private String extractDownloadUrl(Document document) {
        try {
            // 定位包含"Download Mediafire"文本的span元素
            Element spanElement = document.select("span:containsOwn(Download Mediafire)").first();
            if (spanElement == null) {
                logger.warn("未找到包含'Download Mediafire'的span元素");
                return null;
            }

            // 回溯到父级a标签，获取href属性
            Element anchorElement = spanElement.parent();
            while (anchorElement != null && !anchorElement.tagName().equals("a")) {
                anchorElement = anchorElement.parent();
            }

            if (anchorElement == null || !anchorElement.hasAttr("href")) {
                logger.warn("未找到有效的a标签或href属性");
                return null;
            }
            return anchorElement.attr("href");
        } catch (Exception e) {
            logger.error("提取下载链接时发生错误", e);
            return null;
        }
    }

    private Boolean downLoadByURL(String url) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            Document document = Jsoup.connect(url).proxy(proxy)  // 设置代理
                    .userAgent("Mozilla/5.0")
                    .timeout(30000).get();
            // 提取目标URL
            Element downloadLink = document.select("a.input.popsok[aria-label=Download file]").first();
            if (downloadLink != null && downloadLink.hasAttr("href")) {
                String targetUrl = downloadLink.attr("href");
                logger.info("成功提取目标URL: {}", targetUrl);
                // 调用 saveFile 方法保存文件

                return saveFile(targetUrl);
            } else {
                logger.warn("未找到目标下载链接");
                return false;
            }
        } catch (IOException e) {
            logger.error("下载过程中发生错误", e);
            return false;
        }
    }

    /**
     * 保存文件到指定目录
     *
     * @param url 文件下载链接
     */
    private Boolean saveFile(String url) {
        try {
            // 创建 HttpClient 实例
            var httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .proxy(ProxySelector.of(new InetSocketAddress(PROXY_HOST, PROXY_PORT))) // 设置代理
                    .build();

            // 发起 HTTP GET 请求
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // 获取文件名
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            Path filePath = SAVE_DIR.resolve(fileName);

            // 将响应体保存到文件
            HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(filePath));
            if (response.statusCode() == 200) {
                logger.info("文件保存成功: {}", filePath);
                // 解压文件
                String password = JSON_NODE.has("password") ? JSON_NODE.get("password").asText() : null;
                if (password != null) {
                    unzipFile(filePath, password);
                } else {
                    logger.warn("未提供解压密码，跳过解压步骤");
                }
                return true;
            } else {
                logger.warn("文件保存失败，状态码: {}", response.statusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("保存文件时发生错误", e);
            return false;
        }
    }

    /**
     * 解压文件
     *
     * @param filePath 文件路径
     * @param password 解压密码
     */
    private void unzipFile(Path filePath, String password) {
        RandomAccessFile randomAccessFile = null;
        IInArchive archive = null;

        try {
            // 使用 SevenZipJBinding 进行解压
            SevenZip.initSevenZipFromPlatformJAR();
            randomAccessFile = new RandomAccessFile(filePath.toFile(), "r");
            archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));

            int numberOfItems = archive.getNumberOfItems();
            for (int i = 0; i < numberOfItems; i++) {
                // 获取文件路径，避免可能的NPE
                Object pathProperty = archive.getProperty(i, PropID.PATH);
                if (pathProperty == null) {
                    logger.warn("跳过无效路径的文件");
                    continue;
                }

                String itemPath = pathProperty.toString();
                Path outputPath = SAVE_DIR.resolve(itemPath);

                // 检查是否为目录
                boolean isFolder = (Boolean) archive.getProperty(i, PropID.IS_FOLDER);
                if (isFolder) {
                    Files.createDirectories(outputPath);
                    logger.info("创建目录: {}", outputPath);
                    continue;
                }

                // 确保父目录存在
                if (outputPath.getParent() != null) {
                    Files.createDirectories(outputPath.getParent());
                }

                // 使用自定义的输出流适配器
                final Path finalOutputPath = outputPath;
                ExtractOperationResult result = archive.extractSlow(i, new ISequentialOutStream() {
                    @Override
                    public int write(byte[] data) throws SevenZipException {
                        try {
                            // 追加模式写入文件
                            Files.write(finalOutputPath, data, StandardOpenOption.CREATE,
                                    StandardOpenOption.APPEND);
                            return data.length;
                        } catch (IOException e) {
                            throw new SevenZipException("写入文件失败: " + finalOutputPath, e);
                        }
                    }
                }, password);

                if (result == ExtractOperationResult.OK) {
                    logger.info("成功解压文件: {}", outputPath);
                } else {
                    logger.warn("解压文件失败: {}，结果: {}", outputPath, result);
                }
            }
        } catch (Exception e) {
            logger.error("解压文件时发生错误: {}", e.getMessage(), e);
        } finally {
            // 确保资源正确关闭
            try {
                if (archive != null) {
                    archive.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                logger.error("关闭资源时发生错误", e);
            }
        }
    }
}