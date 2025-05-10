package cn.helloworld1999.webcrawler.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component("Fapello")
public class FapelloDownloader implements DownloaderStrategy {
    static String PROXY_HOST = "127.0.0.1";
    static int PROXY_PORT = 7890;
    // 创建信任所有证书的 SSLContext
    private static SSLContext createTrustAllSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            }, new java.security.SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create SSLContext", e);
        }
    }

    // 明确指定代理配置
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .sslContext(createTrustAllSSLContext()) // 使用信任所有证书的 SSLContext
            .proxy(ProxySelector.of(new InetSocketAddress(PROXY_HOST, PROXY_PORT))) // 设置代理
            .build();

    // 日志记录器
    private static final Logger logger = Logger.getLogger(FapelloDownloader.class.getName());

    // 模型名称、起始编号、结束编号、队列名称
    private String MODEL_NAME;
    private int START_NUM;
    private int END_NUM;

    // 路径前缀的第一部分和第二部分
    private String PATH_PREFIX_PART1;
    private String PATH_PREFIX_PART2;
    
    private final String COOKIE = "_ga=GA1.1.767345094.1744132392; wasInt=1; _ga_W0QKNEZ1J9=GS1.1.1744132392.1.1.1744132993.0.0.0";

    // 最大线程数、最大重试次数、支持的文件扩展名
    private final int MAX_THREADS = 10;
    private final int MAX_RETRY = 3;
    private final List<String> EXTENSIONS = Arrays.asList("mp4", "jpg");
    private Path SAVE_DIR; // 文件保存目录

    private Set<String> downloadedFiles = new HashSet<>(); // 已下载文件集合

    /**
     * 初始化已下载文件列表
     */
    private void initDownloadedFiles() {
        try {
            Files.createDirectories(SAVE_DIR); // 创建保存目录
            Files.list(SAVE_DIR).filter(Files::isRegularFile).map(Path::getFileName).map(Path::toString).forEach(downloadedFiles::add);
            logger.info("已加载 " + downloadedFiles.size() + " 个历史文件");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "初始化已下载文件失败", e);
        }
    }

    /**
     * 根据编号和扩展名生成资源 URL
     *
     * @param num 编号
     * @param ext 文件扩展名
     * @return 资源 URL
     */
    private String generateUrl(int num, String ext) {
        int group = (int) Math.ceil((double) num / 1000) * 1000;
        return String.format("https://fapello.com/content/%s/%s/%s/%d/%s_%04d.%s",
                PATH_PREFIX_PART1, PATH_PREFIX_PART2, MODEL_NAME, group, MODEL_NAME, num, ext);
    }

    /**
     * 生成 HTTP 请求头
     *
     * @param num 编号
     * @return 请求头键值对
     */
    private Map<String, String> generateHeaders(int num) {
        return Map.of(
                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36",
                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                "Accept-Language", "zh-CN,zh;q=0.9",
                "Referer", String.format("http://fapello.com/%s/%d/", MODEL_NAME, num),
                "Sec-Fetch-Dest", "document",
                "Sec-Fetch-Mode", "navigate",
                "Sec-Fetch-Site", "same-origin",
                "Upgrade-Insecure-Requests", "1",
                "Cookie", COOKIE
        );
    }

    /**
     * 验证资源是否有效
     *
     * @param url 资源 URL
     * @return 是否有效
     */
    private boolean validateResource(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(generateHeaders(0).entrySet().stream()
                        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                        .toArray(String[]::new))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(15))
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                return false;
            }

            String ext = url.substring(url.lastIndexOf('.') + 1).toLowerCase();
            String contentType = response.headers().firstValue("Content-Type").orElse("");

            if ("mp4".equals(ext) && !contentType.contains("video/mp4")) {
                logger.warning("MP4类型不匹配: " + contentType);
                return false;
            }
            if ("jpg".equals(ext) && !contentType.contains("image/jpeg")) {
                logger.warning("JPG类型不匹配: " + contentType);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.warning("资源验证失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 下载单个资源文件
     *
     * @param url 资源 URL
     */
    private void downloadSingle(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1).replace("..", "_");
        if (downloadedFiles.contains(fileName)) {
            logger.info("跳过已下载: " + fileName);
            return;
        }

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(generateHeaders(0).entrySet().stream()
                                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                                .toArray(String[]::new))
                        .GET()
                        .timeout(Duration.ofMinutes(1))
                        .build();

                HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
                response.body().transferTo(Files.newOutputStream(SAVE_DIR.resolve(fileName)));
                downloadedFiles.add(fileName);
                logger.info("成功下载: " + fileName);
                return;
            } catch (Exception e) {
                logger.warning(String.format("第 %d 次尝试失败: %s - %s", attempt, fileName, e.getMessage()));
                try {
                    Thread.sleep((long) Math.pow(2, attempt) * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        logger.severe("永久失败: " + fileName);
    }

    /**
     * 处理单个编号的任务
     *
     * @param num 编号
     */
    private void processNumber(int num) {
        for (String ext : EXTENSIONS) {
            String url = generateUrl(num, ext);
            if (validateResource(url)) {
                logger.info("开始下载: " + url);
                downloadSingle(url);
                break;
            }
        }
    }

    /**
     * 启动下载任务
     */
    public Boolean start() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ipify.org"))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            String testIp = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            logger.info("代理生效 - 当前出口IP: " + testIp);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "代理连接失败", e);
            return false;
        }

        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        List<Future<?>> futures = new ArrayList<>();

        for (int num = START_NUM; num <= END_NUM; num++) {
            int finalNum = num;
            futures.add(executor.submit(() -> processNumber(finalNum)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "任务异常", e);
                return false;
            }
        }

        executor.shutdown();
        return true;
    }

    /**
     * 记录当前配置信息
     */
    private void logConfig() {
        logger.info(String.format("生效配置 - 模型: %s", MODEL_NAME));
        logger.info(String.format("路径前缀: %s/%s", PATH_PREFIX_PART1, PATH_PREFIX_PART2));
        logger.info(String.format("下载范围: %d~%d", START_NUM, END_NUM));
    }

    public Boolean start(JsonNode jsonNode) {
        this.MODEL_NAME = jsonNode.has("model_name") ? jsonNode.get("model_name").asText(MODEL_NAME) : MODEL_NAME;
        String pathPrefix = MODEL_NAME.substring(0, 1).toLowerCase() + MODEL_NAME.substring(1);
        this.PATH_PREFIX_PART1 = pathPrefix.substring(0, 1).toLowerCase();
        this.PATH_PREFIX_PART2 = pathPrefix.length() > 1 ? pathPrefix.substring(1, 2).toLowerCase() : PATH_PREFIX_PART1;
        this.START_NUM = jsonNode.has("start_num") ? jsonNode.get("start_num").asInt(START_NUM) : START_NUM;
        this.END_NUM = jsonNode.has("end_num") ? jsonNode.get("end_num").asInt(END_NUM) : END_NUM;
        this.SAVE_DIR = Paths.get("\\\\192.168.31.103\\存储空间\\2.2.爬虫").resolve(MODEL_NAME);
        // 初始化已下载文件列表
        initDownloadedFiles();

        // 记录日志，显示接收到的任务信息
        logger.info(String.format("收到任务: %s %d-%d", MODEL_NAME, START_NUM, END_NUM));
        this.logConfig();
        // 启动下载任务
        return start();
    }
}