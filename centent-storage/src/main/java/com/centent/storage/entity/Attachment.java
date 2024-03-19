package com.centent.storage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.centent.core.define.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.util.Strings;

@Data
@TableName("attachment")
@EqualsAndHashCode(callSuper = true)
public class Attachment extends BaseEntity {

    /**
     * 文件名称
     *
     * @since 0.0.1
     */
    private String name;

    /**
     * 文件类型
     *
     * @since 0.0.1
     */
    private String type;

    /**
     * 文件大小
     *
     * @since 0.0.1
     */
    private Long size;

    public Attachment(String hash) {
        this.id = hash;
    }

    public String getStoredFileName() {
        if (Strings.isNotBlank(this.getType())) {
            return this.getId() + "." + this.getType();
        }
        return this.getId();
    }
}
