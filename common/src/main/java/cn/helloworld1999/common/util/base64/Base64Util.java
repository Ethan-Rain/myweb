package cn.helloworld1999.common.util.base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Base64 工具类：支持字符串、文件、二进制数据的编码/解码
 */
public class Base64Util {

    // ------------------------ 字符串 ------------------------

    /**
     * 字符串 → Base64
     */
    public static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 → 字符串
     */
    public static String decode(String base64) {
        return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
    }

    // ------------------------ 二进制数据 ------------------------

    /**
     * 字节数组 → Base64
     */
    public static String encode(byte[] binaryData) {
        return Base64.getEncoder().encodeToString(binaryData);
    }

    /**
     * Base64 → 字节数组
     */
    public static byte[] decodeToBytes(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    // ------------------------ 文件 ------------------------

    /**
     * 文件 → Base64
     *
     * @param filePath 文件路径（如："/data/test.pdf"）
     */
    public static String encodeFile(String filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        return encode(fileBytes); // 复用二进制编码方法
    }

    /**
     * Base64 → 文件
     *
     * @param base64     Base64字符串
     * @param outputPath 输出文件路径（如："/data/decrypted.pdf"）
     */
    public static void decodeToFile(String base64, String outputPath) throws IOException {
        byte[] fileBytes = decodeToBytes(base64);
        Files.write(Paths.get(outputPath), fileBytes);
    }
}