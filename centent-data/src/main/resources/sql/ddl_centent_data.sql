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

CREATE TABLE IF NOT EXISTS identity_address_change
(
    id          VARCHAR(32) PRIMARY KEY,
    code        INT       NOT NULL,
    new_code    INT       NOT NULL,
    time        INT,
    remark      VARCHAR(4000),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE identity_address_change IS '中华人民共和国行政区划-变化数据表';
COMMENT ON COLUMN identity_address_change.id IS '主键';
COMMENT ON COLUMN identity_address_change.code IS '原行政区划代码';
COMMENT ON COLUMN identity_address_change.new_code IS '新行政区划代码';
COMMENT ON COLUMN identity_address_change.time IS '变化年份';
COMMENT ON COLUMN identity_address_change.remark IS '备注';
COMMENT ON COLUMN identity_address_change.create_time IS '创建时间';
COMMENT ON COLUMN identity_address_change.update_time IS '更新时间';
