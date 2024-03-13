package com.centent.storage;

import com.centent.core.exception.BusinessException;
import com.centent.core.util.FileUtil;
import com.centent.storage.entity.Attachment;
import com.centent.storage.mapper.AttachmentMapper;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class IStorage {

    @Resource
    private AttachmentMapper attachmentMapper;

    protected abstract void upload0(Attachment attachment, File file) throws IOException;

    protected abstract void upload0(Attachment attachment, MultipartFile file) throws IOException;

    protected abstract File get0(Attachment attachment);

    public abstract String getFilePath(String fileId);

    @Transactional
    public Attachment upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传失败，文件为空");
        }
        // 计算文件hash
        String hash = FileUtil.hash(file);

        Attachment attachment = this.getAttachment(hash, file.getOriginalFilename(), file.getSize());

        // 上传时间和更新时间一致，标识该文件为新文件，需要执行文件上传
        if (attachment.getCreateTime().isEqual(attachment.getUpdateTime())) {
            try {
                this.upload0(attachment, file);
            } catch (IOException e) {
                throw new BusinessException("上传文件失败！", e);
            }
        }
        return attachment;
    }

    @Transactional
    public Attachment upload(File file) {
        // 计算文件hash
        String hash = FileUtil.hash(file);

        Attachment attachment = this.getAttachment(hash, file.getName(), file.length());

        // 上传时间和更新时间一致，标识该文件为新文件，需要执行文件上传
        if (attachment.getCreateTime().isEqual(attachment.getUpdateTime())) {
            try {
                this.upload0(attachment, file);
            } catch (IOException e) {
                throw new BusinessException("上传文件失败！", e);
            }
        }
        return attachment;
    }

    public File get(String fileId) {
        Attachment attachment = attachmentMapper.selectById(fileId);
        if (Objects.isNull(attachment)) {
            throw new BusinessException("文件ID不存在：" + fileId);
        }
        return this.get0(attachment);
    }

    private Attachment getAttachment(String hash, String name, Long size) {
        Attachment attachment = attachmentMapper.selectById(hash);

        boolean exists = Objects.nonNull(attachment);
        if (!exists) {
            attachment = new Attachment(hash);
            attachment.setSize(size);
        }

        if (Objects.isNull(name)) {
            name = "";
        }
        attachment.setName(name);

        if (name.contains(".")) {
            attachment.setType(name.substring(name.lastIndexOf(".")));
        }
        if (exists) {
            attachmentMapper.updateById(attachment);
        } else {
            attachmentMapper.insert(attachment);
        }
        return attachment;
    }
}
