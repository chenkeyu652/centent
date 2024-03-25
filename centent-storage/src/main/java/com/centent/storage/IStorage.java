package com.centent.storage;

import com.centent.core.exception.BusinessException;
import com.centent.core.exception.NotFoundException;
import com.centent.core.util.CententUtil;
import com.centent.core.util.FileUtil;
import com.centent.storage.entity.Attachment;
import com.centent.storage.mapper.AttachmentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public abstract class IStorage {

    @Resource
    private AttachmentMapper attachmentMapper;

    protected abstract File upload0(Attachment attachment, File file) throws IOException;

    protected abstract File upload0(Attachment attachment, MultipartFile file) throws IOException;

    protected abstract File get0(Attachment attachment);

    public abstract String getFilePath(String fileId);

    @Transactional
    public Attachment upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传失败，文件为空");
        }
        // 计算文件hash
        String hash = FileUtil.hash(file);

        return this.doUpload(file, hash, file.getOriginalFilename(), file.getSize());
    }

    @Transactional
    public Attachment upload(File file) {
        // 计算文件hash
        String hash = FileUtil.hash(file);
        return this.doUpload(file, hash, file.getName(), file.length());
    }

    private Attachment doUpload(Object file, String hash, String fileName, long size) {
        Attachment attachment = this.getAttachment(hash, fileName, size);

        if (!attachment.isNew()) {
            try {
                File storedFile = this.get0(attachment);
                attachment.setFile(storedFile);
            } catch (NotFoundException e) {
                log.error("文件不存在：" + attachment.getId(), e);
                // 增加防护，如果实体文件不存在，就再上传一次
                attachment.setNew(true);
            }
        }

        // 需要执行文件上传
        if (attachment.isNew()) {
            try {
                File destFile;
                if (file instanceof MultipartFile) {
                    destFile = this.upload0(attachment, (MultipartFile) file);
                } else {
                    destFile = this.upload0(attachment, (File) file);
                }
                attachment.setFile(destFile);
            } catch (IOException e) {
                throw new BusinessException("上传文件失败！", e);
            }
        }
        return attachment;
    }

    public Attachment get(String fileId) {
        Attachment attachment = attachmentMapper.selectById(fileId);
        if (Objects.isNull(attachment)) {
            throw new BusinessException("文件ID不存在：" + fileId);
        }
        attachment.setFile(this.get0(attachment));
        return attachment;
    }

    private Attachment getAttachment(String hash, String fileName, Long size) {
        Attachment attachment = attachmentMapper.selectById(hash);
        if (Objects.isNull(attachment)) {
            attachment = new Attachment(hash);
            fileName = CententUtil.initialized(fileName) ? fileName : "";
            attachment.setSize(size);
            attachment.setName(fileName);
            if (fileName.contains(".")) {
                String type = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                attachment.setType(type);
            }
            attachmentMapper.insert(attachment);
        }
        return attachment;
    }
}
