package cn.helloworld1999.mediaservice.service.impl;

import cn.helloworld1999.mediaservice.config.SynologyConfig;
import cn.helloworld1999.mediaservice.service.SynologyFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
@Service
public class SynologyFileServiceImpl implements SynologyFileService {

    @Autowired
    private SynologyConfig synologyConfig;

    private String sid;

    // 根据配置返回 base url（包含 scheme://host:port）
    private String getBaseUrl() {
        String scheme = synologyConfig.isUseHttps() ? "https" : "http";
        int port = synologyConfig.getPort();
        if (port <= 0) {
            port = synologyConfig.isUseHttps() ? 5001 : 5000;
        }
        // 如果显式配置的端口与 scheme 的惯用端口不一致，记录警告
        if (synologyConfig.isUseHttps() && port != 5001) {
            log.warn("synology.useHttps=true but port={} (default 5001 for HTTPS)", port);
        } else if (!synologyConfig.isUseHttps() && port != 5000) {
            log.warn("synology.useHttps=false but port={} (default 5000 for HTTP)", port);
        }
        return String.format("%s://%s:%d", scheme, synologyConfig.getHost(), port);
    }

    // 延迟创建 RestTemplate，根据 skipSsl 决定是否跳过证书验证（仅测试用）
    private RestTemplate createRestTemplate() {
        if (synologyConfig.isUseHttps() && synologyConfig.isSkipSsl()) {
            try {
                disableSslVerification();
                log.warn("Synology skipSsl=true: SSL certificate validation disabled (testing only)");
            } catch (Exception e) {
                log.error("Failed to disable SSL verification", e);
            }
        }
        return new RestTemplate();
    }

    // 关闭证书验证（会设置全局默认 SSLSocketFactory 和 HostnameVerifier）
    private static volatile boolean sslDisabled = false;
    private static synchronized void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {
        if (sslDisabled) return;
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        sslDisabled = true;
    }

    private void login() {
        String base = getBaseUrl();
        String url = String.format("%s/webapi/auth.cgi", base);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("api", "SYNO.API.Auth")
            .queryParam("version", synologyConfig.getApiVersion())
            .queryParam("method", "login")
            .queryParam("account", synologyConfig.getUsername())
            .queryParam("passwd", synologyConfig.getPassword())
            .queryParam("session", synologyConfig.getSessionName());

        RestTemplate restTemplate = createRestTemplate();

        ResponseEntity<Map> response;
        try {
            response = restTemplate.getForEntity(builder.toUriString(), Map.class);
        } catch (RestClientException e) {
            log.error("Failed to call Synology auth API, url={}", builder.toUriString(), e);
            throw new IllegalStateException("Synology auth request failed", e);
        }

        Map body = response.getBody();
        if (body == null) {
            log.error("Synology auth response body is null. URL={}", builder.toUriString());
            throw new IllegalStateException("Synology auth response is empty");
        }

        Boolean success = (Boolean) body.get("success");
        if (success == null || !success) {
            log.error("Synology auth failed. response={}", body);
            throw new IllegalStateException("Synology auth failed: " + body);
        }

        Map<String, Object> data = (Map<String, Object>) body.get("data");
        if (data == null) {
            log.error("Synology auth response data is null. response={}", body);
            throw new IllegalStateException("Synology auth response missing data: " + body);
        }

        Object sidObj = data.get("sid");
        if (sidObj == null) {
            log.error("Synology auth response missing sid. data={}", data);
            throw new IllegalStateException("Synology auth did not return sid: " + data);
        }

        this.sid = String.valueOf(sidObj);
        log.info("Synology login successful, sid set");
    }

    @Override
    public List<Map<String, Object>> listFiles(String path, boolean recursive) {
        // 尝试2次：第一次如遇授权失败则重新login再试一次
        for (int attempt = 0; attempt < 2; attempt++) {
            if (sid == null) {
                login();
            }

            String base = getBaseUrl();
            String url = String.format("%s/webapi/entry.cgi", base);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("api", "SYNO.FileStation.List")
                .queryParam("version", "2")
                .queryParam("method", "list")
                .queryParam("folder_path", path)
                .queryParam("_sid", sid)
                .queryParam("additional", "real_path,size,time,perm")
                .queryParam("recursive", recursive);

            RestTemplate restTemplate = createRestTemplate();

            ResponseEntity<Map> response;
            try {
                response = restTemplate.getForEntity(builder.toUriString(), Map.class);
            } catch (RestClientException e) {
                log.error("Failed to call Synology list API, url={}", builder.toUriString(), e);
                throw new IllegalStateException("Synology list request failed", e);
            }

            Map body = response.getBody();
            if (body == null) {
                log.error("Synology list response body is null. URL={}", builder.toUriString());
                throw new IllegalStateException("Synology list response is empty");
            }

            Boolean success = (Boolean) body.get("success");
            if (success == null) {
                log.error("Synology list response missing success flag. response={}", body);
                throw new IllegalStateException("Synology list response missing success flag: " + body);
            }

            if (!success) {
                // 可能是sid失效或权限问题，记录并尝试重新登录一次
                log.warn("Synology list API returned success=false. response={}", body);
                // 清空 sid 以触发下一次循环的 login()
                this.sid = null;
                continue; // 重试一次
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data == null) {
                log.error("Synology list response data is null. response={}", body);
                throw new IllegalStateException("Synology list response missing data: " + body);
            }

            List<Map<String, Object>> files = (List<Map<String, Object>>) data.get("files");
            return files != null ? files : new ArrayList<>();
        }

        throw new IllegalStateException("Failed to list files from Synology after retries");
    }

    @Override
    public Map<String, Object> getFileInfo(String path) {
        for (int attempt = 0; attempt < 2; attempt++) {
            if (sid == null) {
                login();
            }

            String base = getBaseUrl();
            String url = String.format("%s/webapi/entry.cgi", base);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("api", "SYNO.FileStation.List")
                .queryParam("version", "2")
                .queryParam("method", "getinfo")
                .queryParam("path", path)
                .queryParam("_sid", sid)
                .queryParam("additional", "real_path,size,time,perm");

            RestTemplate restTemplate = createRestTemplate();

            ResponseEntity<Map> response;
            try {
                response = restTemplate.getForEntity(builder.toUriString(), Map.class);
            } catch (RestClientException e) {
                log.error("Failed to call Synology getinfo API, url={}", builder.toUriString(), e);
                throw new IllegalStateException("Synology getinfo request failed", e);
            }

            Map body = response.getBody();
            if (body == null) {
                log.error("Synology getinfo response body is null. URL={}", builder.toUriString());
                throw new IllegalStateException("Synology getinfo response is empty");
            }

            Boolean success = (Boolean) body.get("success");
            if (success == null) {
                log.error("Synology getinfo response missing success flag. response={}", body);
                throw new IllegalStateException("Synology getinfo response missing success flag: " + body);
            }

            if (!success) {
                log.warn("Synology getinfo API returned success=false. response={}", body);
                this.sid = null;
                continue;
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data == null) {
                log.error("Synology getinfo response data is null. response={}", body);
                throw new IllegalStateException("Synology getinfo response missing data: " + body);
            }

            List<Map<String, Object>> files = (List<Map<String, Object>>) data.get("files");
            return files != null && !files.isEmpty() ? files.get(0) : null;
        }

        throw new IllegalStateException("Failed to get file info from Synology after retries");
    }
}
