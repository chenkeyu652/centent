CREATE TABLE IF NOT EXISTS identity_address
(
    id          VARCHAR(32) PRIMARY KEY,
    code        INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    start       INT,
    ends        INT,
    remark      VARCHAR(4000),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE identity_address IS '中华人民共和国行政区划表';
COMMENT ON COLUMN identity_address.id IS '主键';
COMMENT ON COLUMN identity_address.code IS '行政区划代码';
COMMENT ON COLUMN identity_address.name IS '行政区划名称';
COMMENT ON COLUMN identity_address.start IS '启用年份';
COMMENT ON COLUMN identity_address.ends IS '废止年份';
COMMENT ON COLUMN identity_address.remark IS '备注';
COMMENT ON COLUMN identity_address.create_time IS '创建时间';
COMMENT ON COLUMN identity_address.update_time IS '更新时间';
