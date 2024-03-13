package com.centent.storage.service;

import com.centent.core.exception.BusinessException;
import com.centent.storage.entity.Attachment;
import com.centent.storage.mapper.AttachmentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AttachmentService {

    @Resource
    private AttachmentMapper mapper;

    public Attachment selectById(String fileId) {
        Attachment attachment = mapper.selectById(fileId);
        if (Objects.isNull(attachment)) {
            throw new BusinessException("文件不存在：" + fileId);
        }
        return attachment;
    }

    public int insert(Attachment attachment) {
        return mapper.insert(attachment);
    }

    public int update(Attachment attachment) {
        return mapper.updateById(attachment);
    }
}
