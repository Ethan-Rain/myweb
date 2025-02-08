package cn.helloworld1999.myweb.demos.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Component
public class NetworkInfoUtil {
    private static final Logger log = LoggerFactory.getLogger(NetworkInfoUtil.class);

    @Value("${server.port:8080}")
    private int port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @PostConstruct
    public void printNetworkInfo() {
        try {
            List<String> ips = getLocalIPs();
            String divider = "================================================";

            log.info(divider);
            log.info("Application Network Information:");
            ips.forEach(ip -> {
                String url = buildAccessUrl(ip);
                log.info("Local IP: {} → Access URL: {}", ip, url);
                generateQrCode(url); // 生成二维码
            });
            log.info(divider);
        } catch (SocketException e) {
            log.error("Failed to get network information", e);
        }
    }

    // 新增的二维码生成方法
    private void generateQrCode(String url) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 200, 200);

            String qrCode = String.valueOf(MatrixToImageWriter.toBufferedImage(matrix));
            log.info("\nQR Code for {}:\n{}\n", url, qrCode);
        } catch (Exception e) {
            log.warn("QR code generation failed: {}", e.getMessage());
        }
    }

    private List<String> getLocalIPs() throws SocketException {
        List<String> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (isValidInterface(ni)) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        ipList.add(addr.getHostAddress());
                    }
                }
            }
        }
        return ipList;
    }

    private boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() &&
               !ni.isVirtual() &&
               ni.isUp() &&
               !ni.getDisplayName().contains("VirtualBox") &&
               !ni.getDisplayName().contains("VMware");
    }

    private String buildAccessUrl(String ip) {
        String baseUrl = String.format("http://%s:%d%s", ip, port, contextPath);
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
