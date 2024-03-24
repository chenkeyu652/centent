CREATE TABLE IF NOT EXISTS wechat_official_user
(
    id          VARCHAR(32) PRIMARY KEY,
    openid      VARCHAR(255) UNIQUE NOT NULL,
    unionid     VARCHAR(255),
    nickname    VARCHAR(255),
    status      INT                 NOT NULL,
    rule        INT                 NOT NULL,
    remark      VARCHAR(4000),
    create_time TIMESTAMP           NOT NULL,
    update_time TIMESTAMP           NOT NULL
);

COMMENT ON TABLE wechat_official_user IS '微信公众号用户信息表';
COMMENT ON COLUMN wechat_official_user.id IS '主键';
COMMENT ON COLUMN wechat_official_user.openid IS '用户openid';
COMMENT ON COLUMN wechat_official_user.unionid IS '用户unionid';
COMMENT ON COLUMN wechat_official_user.nickname IS '用户昵称';
COMMENT ON COLUMN wechat_official_user.status IS '用户状态';
COMMENT ON COLUMN wechat_official_user.rule IS '用户角色';
COMMENT ON COLUMN wechat_official_user.remark IS '备注';
COMMENT ON COLUMN wechat_official_user.create_time IS '创建时间';
COMMENT ON COLUMN wechat_official_user.update_time IS '更新时间';
