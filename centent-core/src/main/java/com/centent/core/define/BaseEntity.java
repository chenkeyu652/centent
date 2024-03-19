package com.centent.core.define;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 基础实体
 *
 * @since 0.0.1
 */
@Data
public abstract class BaseEntity {

    /**
     * 数据ID
     *
     * @since 0.0.1
     */
    @TableId(type = IdType.ASSIGN_ID)
    protected String id;

    /**
     * 备注
     *
     * @since 0.0.1
     */
    protected String remark;

    /**
     * 创建时间
     *
     * @since 0.0.1
     */
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * 更新时间
     *
     * @since 0.0.1
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

    /**
     * 自定义mybatis-plus填充插入时间和更新时间
     *
     * @since 0.0.1
     */
    @Slf4j
    @Component
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        /**
         * 插入时的填充策略
         *
         * @param meta MetaObject
         * @since 0.0.1
         */
        @Override
        public void insertFill(MetaObject meta) {
            LocalDateTime now = LocalDateTime.now();
            this.setFieldValByName("createTime", now, meta);
            this.setFieldValByName("updateTime", now, meta);
        }

        /**
         * 更新时的填充策略
         *
         * @param meta MetaObject
         * @since 0.0.1
         */
        @Override
        public void updateFill(MetaObject meta) {
            this.setFieldValByName("updateTime", LocalDateTime.now(), meta);
        }
    }
}
