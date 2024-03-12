CREATE TABLE IF NOT EXISTS attachment
(
    id          VARCHAR(32) PRIMARY KEY,
    name        VARCHAR(255),
    type        VARCHAR(255),
    size        BIGINT,
    remark      VARCHAR(4000),
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL
);

COMMENT ON TABLE attachment IS '附件信息表';
COMMENT ON COLUMN attachment.id IS '主键';
COMMENT ON COLUMN attachment.name IS '附件名称';
COMMENT ON COLUMN attachment.type IS '附件类型';
COMMENT ON COLUMN attachment.size IS '附件大小';
COMMENT ON COLUMN attachment.remark IS '备注';
COMMENT ON COLUMN attachment.create_time IS '创建时间';
COMMENT ON COLUMN attachment.update_time IS '更新时间';