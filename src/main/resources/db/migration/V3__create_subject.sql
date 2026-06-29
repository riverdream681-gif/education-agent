-- V3: RBAC 学科权限表
CREATE TABLE IF NOT EXISTS subject (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    tenant_id  BIGINT
);
COMMENT ON TABLE  subject          IS '学科表';
COMMENT ON COLUMN subject.name     IS '学科名称';
COMMENT ON COLUMN subject.tenant_id IS '租户 ID（预留）';

CREATE TABLE IF NOT EXISTS user_subject_relation (
    id         BIGSERIAL  PRIMARY KEY,
    user_id    BIGINT     NOT NULL,
    subject_id BIGINT     NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_usr_user_id    ON user_subject_relation(user_id);
CREATE INDEX IF NOT EXISTS idx_usr_subject_id ON user_subject_relation(subject_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_usr_user_subject ON user_subject_relation(user_id, subject_id);

-- 初始化学科
INSERT INTO subject (name) VALUES ('高中数学') ON CONFLICT DO NOTHING;
INSERT INTO subject (name) VALUES ('高中物理') ON CONFLICT DO NOTHING;
INSERT INTO subject (name) VALUES ('高中化学') ON CONFLICT DO NOTHING;

-- 测试用户 student1 / 123456
INSERT INTO edu_user (username, password, nickname, status)
VALUES ('student1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试学生', 1)
ON CONFLICT DO NOTHING;

-- 给测试用户分配全部学科
INSERT INTO user_subject_relation (user_id, subject_id)
SELECT u.id, s.id FROM edu_user u CROSS JOIN subject s
WHERE u.username = 'student1'
ON CONFLICT DO NOTHING;
