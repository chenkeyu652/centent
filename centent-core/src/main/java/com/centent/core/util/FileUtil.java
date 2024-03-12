package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class FileUtil {

    private static final int BUFFER_SIZE = 4096; // 缓冲区大小，可以根据实际需求调整

    private static final String HASH_ALGORITHM = "SHA-256"; // 选择你想要的哈希算法

    public static String hash(File file, String hashAlgorithm) {
        try {
            try (InputStream fis = new FileInputStream(file)) {
                return calculateHash(fis, hashAlgorithm);
            }
        } catch (Exception e) {
            log.error("计算文件hash失败", e);
        }
        return null;
    }

    public static String hash(String filePath, String hashAlgorithm) {
        try {
            try (InputStream fis = new FileInputStream(filePath)) {
                return calculateHash(fis, hashAlgorithm);
            }
        } catch (Exception e) {
            log.error("计算文件hash失败", e);
        }
        return null;
    }

    private static String hash(MultipartFile file, String hashAlgorithm) {
        try {
            try (InputStream fis = file.getInputStream()) {
                return calculateHash(fis, hashAlgorithm);
            }
        } catch (Exception e) {
            log.error("计算文件hash失败", e);
        }
        return null;
    }

    public static String hash(File file) {
        return hash(file, HASH_ALGORITHM);
    }

    public static String hash(String filePath) {
        return hash(filePath, HASH_ALGORITHM);
    }

    public static String hash(MultipartFile file) {
        return hash(file, HASH_ALGORITHM);
    }

    private static String calculateHash(InputStream fis, String hashAlgorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
        byte[] byteArray = new byte[BUFFER_SIZE];

        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }


        // 获得哈希值
        byte[] hash = digest.digest();

        // 以十六进制格式显示哈希值
        StringBuilder sb = new StringBuilder();
        for (byte aHash : hash) {
            sb.append(Integer.toString((aHash & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
