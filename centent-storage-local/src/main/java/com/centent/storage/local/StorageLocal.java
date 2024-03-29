package com.centent.storage.local;

import com.centent.core.exception.BusinessException;
import com.centent.core.exception.NotFoundException;
import com.centent.storage.IStorage;
import com.centent.storage.entity.Attachment;
import com.centent.storage.local.bean.StorageLocalConfig;
import com.centent.storage.service.AttachmentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StorageLocal extends IStorage {

    private File dest;

    @Resource
    private StorageLocalConfig config;

    @Resource
    private AttachmentService attachmentService;

    @PostConstruct
    public void init() throws IOException {
        dest = new File(config.getRootPath());
        if (!dest.exists() && !dest.mkdirs()) {
            throw new IOException("文件夹创建失败：" + dest.getAbsolutePath());
        }
    }

    @Override
    public File upload0(Attachment attachment, File file) {
        // 保存文件到dest下
        File destFile = new File(dest, attachment.getId());
        try {
            Files.copy(file.toPath(), destFile.toPath());
        } catch (IOException e) {
            throw new BusinessException(e);
        }
        return destFile;
    }

    @Override
    public File upload0(Attachment attachment, MultipartFile file) throws IOException {
        // 保存文件到dest下
        File destFile = new File(dest, attachment.getId());
        file.transferTo(destFile);
        return destFile;
    }

    @Override
    public File get0(Attachment attachment) {
        File target = new File(dest, attachment.getId());
        if (!target.exists()) {
            throw new NotFoundException("文件不存在：" + attachment.getId());
        }
        return target;
    }

    @Override
    public String getFilePath(String fileId) {
        Attachment attachment = attachmentService.selectById(fileId);
        return dest.getAbsolutePath() + File.separator + attachment.getId();
    }
}
