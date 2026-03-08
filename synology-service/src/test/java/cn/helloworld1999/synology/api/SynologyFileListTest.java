package cn.helloworld1999.synology.api;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

public class SynologyFileListTest {

    public static void main(String[] args) throws Exception {
        String baseUrl = "https://192.168.31.103:5001/webapi/entry.cgi";

        String sid = "rbvkgu5w2zQGg15SA6Eo1PLf_U1mlw9gn1WYU4pvJ68-KoQzWHaInmqd1yjrtM0TCYTRlUU1xYyQbb7Mq9NC2Y";
        String folderPath = "/home";

        String url = baseUrl + "?api=SYNO.FileStation.List"
                + "&version=3"
                + "&method=list"
                + "&folder_path=" + folderPath
                + "&_sid=" + sid;

        // 关键：自定义 HttpClient，忽略证书
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(builder.build())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);

        String response = restTemplate.getForObject(url, String.class);
        System.out.println("请求URL: " + url);
        System.out.println("响应结果: " + response);
    }
}
