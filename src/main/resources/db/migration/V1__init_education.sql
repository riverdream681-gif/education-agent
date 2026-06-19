-- ============================================================
-- V1: 初始化 education 模块基础表
-- ============================================================

CREATE TABLE IF NOT EXISTS edu_user (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    nickname        VARCHAR(50),
    email           VARCHAR(100),
    avatar          VARCHAR(500),
    phone           VARCHAR(20),
    status          INT             NOT NULL DEFAULT 1,
    last_login_time TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         INT             NOT NULL DEFAULT 0
);

COMMENT ON TABLE  edu_user               IS 'education 模块用户表';
COMMENT ON COLUMN edu_user.username      IS '用户名，唯一';
COMMENT ON COLUMN edu_user.password      IS 'BCrypt 加密密码';
COMMENT ON COLUMN edu_user.status        IS '状态：0=禁用，1=正常';
COMMENT ON COLUMN edu_user.last_login_time IS '最近登录时间';
COMMENT ON COLUMN edu_user.deleted       IS '逻辑删除：0=未删，1=已删';

CREATE UNIQUE INDEX IF NOT EXISTS idx_edu_user_username ON edu_user(username) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_edu_user_status ON edu_user(status);
CREATE INDEX IF NOT EXISTS idx_edu_user_created_at ON edu_user(created_at);

CREATE TABLE IF NOT EXISTS edu_image_file (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    file_name       VARCHAR(255)    NOT NULL,
    file_path       VARCHAR(500)    NOT NULL,
    file_hash       VARCHAR(64),
    file_size       BIGINT          NOT NULL DEFAULT 0,
    mime_type       VARCHAR(100),
    storage_type    INT             NOT NULL DEFAULT 0,
    ocr_status      INT             NOT NULL DEFAULT 0,
    etl_status      INT             NOT NULL DEFAULT 0,
    ocr_etl_at      TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         INT             NOT NULL DEFAULT 0
);

COMMENT ON TABLE  edu_image_file              IS 'education 模块文件表';
COMMENT ON COLUMN edu_image_file.user_id      IS '上传用户 ID';
COMMENT ON COLUMN edu_image_file.file_name    IS '原始文件名';
COMMENT ON COLUMN edu_image_file.file_path    IS '存储路径';
COMMENT ON COLUMN edu_image_file.file_hash    IS 'SHA-256 文件哈希';
COMMENT ON COLUMN edu_image_file.file_size    IS '文件大小（字节）';
COMMENT ON COLUMN edu_image_file.mime_type    IS 'MIME 类型';
COMMENT ON COLUMN edu_image_file.storage_type IS '存储类型：0=Local，1=OSS，2=MinIO';
COMMENT ON COLUMN edu_image_file.ocr_status   IS 'OCR 状态：0=未识别，1=处理中，2=成功，3=失败';
COMMENT ON COLUMN edu_image_file.etl_status   IS 'ETL 状态：0=未处理，1=处理中，2=成功，3=失败';
COMMENT ON COLUMN edu_image_file.deleted      IS '逻辑删除：0=未删，1=已删';

CREATE INDEX IF NOT EXISTS idx_edu_img_user_id ON edu_image_file(user_id) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_edu_img_file_hash ON edu_image_file(file_hash) WHERE deleted = 0 AND file_hash IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_edu_img_ocr_status ON edu_image_file(ocr_status);
CREATE INDEX IF NOT EXISTS idx_edu_img_etl_status ON edu_image_file(etl_status);
CREATE INDEX IF NOT EXISTS idx_edu_img_created_at ON edu_image_file(created_at);

CREATE TABLE IF NOT EXISTS edu_wrong_question (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    image_id        BIGINT,
    subject         VARCHAR(50),
    tags            VARCHAR(500),
    remark          TEXT,
    ai_status       INT             NOT NULL DEFAULT 0,
    ai_analysis_at  TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         INT             NOT NULL DEFAULT 0
);

COMMENT ON TABLE  edu_wrong_question              IS 'education 模块错题记录表';
COMMENT ON COLUMN edu_wrong_question.user_id      IS '所属用户 ID';
COMMENT ON COLUMN edu_wrong_question.image_id     IS '来源图片 ID';
COMMENT ON COLUMN edu_wrong_question.subject      IS '学科';
COMMENT ON COLUMN edu_wrong_question.tags         IS '标签';
COMMENT ON COLUMN edu_wrong_question.remark       IS '用户备注';
COMMENT ON COLUMN edu_wrong_question.ai_status    IS 'AI 分析状态：0=未分析，1=分析中，2=完成，3=失败';
COMMENT ON COLUMN edu_wrong_question.deleted      IS '逻辑删除：0=未删，1=已删';

CREATE INDEX IF NOT EXISTS idx_edu_wq_user_id ON edu_wrong_question(user_id) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_edu_wq_image_id ON edu_wrong_question(image_id) WHERE deleted = 0 AND image_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_edu_wq_subject ON edu_wrong_question(subject) WHERE deleted = 0 AND subject IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_edu_wq_ai_status ON edu_wrong_question(ai_status);
CREATE INDEX IF NOT EXISTS idx_edu_wq_created_at ON edu_wrong_question(created_at);
CREATE INDEX IF NOT EXISTS idx_edu_wq_user_created ON edu_wrong_question(user_id, created_at DESC) WHERE deleted = 0;
