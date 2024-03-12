package com.centent.storage.local;

import com.centent.core.exception.BusinessException;
import com.centent.storage.IStorage;
import com.centent.storage.entity.Attachment;
import com.centent.storage.local.bean.StorageLocalConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class StorageLocal extends IStorage {

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
    public void upload0(Attachment attachment, File file) {
    }

    @Override
    public void upload0(Attachment attachment, MultipartFile file) throws IOException {
        // 保存文件到dest下
        File destFile = new File(dest, attachment.getStoredFileName());
        file.transferTo(destFile);
    }

    @Override
    public File get0(Attachment attachment) {
        File target = new File(dest, attachment.getStoredFileName());
        if (!target.exists()) {
            throw new BusinessException("文件不存在：" + attachment.getStoredFileName());
        }
        return target;
    }
}
