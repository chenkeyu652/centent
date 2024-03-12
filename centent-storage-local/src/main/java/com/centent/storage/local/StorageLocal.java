package com.centent.storage.local;

import com.centent.core.exception.BusinessException;
import com.centent.storage.IStorage;
import com.centent.storage.local.bean.StorageLocalConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class StorageLocal implements IStorage {

    private File dest;

    @Resource
    private StorageLocalConfig config;

    @PostConstruct
    public void init() throws IOException {
        dest = new File(config.getRootPath());
        if (!dest.exists() && !dest.mkdirs()) {
            throw new IOException("文件夹创建失败：" + dest.getAbsolutePath());
        }
    }

    @Override
    public String upload(File file) {
        return null;
    }

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传失败，文件为空");
        }

        try {
            // 生成唯一的文件名
            Long fileId = UUID.randomUUID().getMostSignificantBits();
            // 获取文件后缀
            String suffix = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = fileId + suffix;

            // 保存文件到dest下
            File destFile = new File(dest, fileName);
            file.transferTo(destFile);

            // 返回 fileName 给前端
            return fileName;
        } catch (IOException e) {
            throw new BusinessException("上传失败: " + e.getMessage());
        }
    }

    @Override
    public File get(String fileName) {
        File target = new File(dest, fileName);
        if (!target.exists()) {
            throw new BusinessException("文件不存在：" + fileName);
        }
        return target;
    }
}
