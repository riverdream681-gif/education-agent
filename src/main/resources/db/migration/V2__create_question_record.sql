-- V2: MVP 阶段错题记录表
CREATE TABLE IF NOT EXISTS question_record (
    id               BIGSERIAL       PRIMARY KEY,
    user_id          BIGINT,
    subject_id       BIGINT,
    question_content TEXT            NOT NULL,
    answer_content   TEXT,
    analysis_result  TEXT,
    knowledge_point  VARCHAR(200),
    is_correct       BOOLEAN,
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  question_record               IS 'MVP 错题记录';
COMMENT ON COLUMN question_record.user_id       IS '提交用户 ID，Phase 1 为 NULL';
COMMENT ON COLUMN question_record.subject_id    IS '学科 ID';
COMMENT ON COLUMN question_record.question_content IS '错题原文';
COMMENT ON COLUMN question_record.analysis_result IS 'LLM 分析结果原文';
COMMENT ON COLUMN question_record.knowledge_point  IS '召回的知识点名称';

CREATE INDEX IF NOT EXISTS idx_qr_user_id ON question_record(user_id);
CREATE INDEX IF NOT EXISTS idx_qr_subject_id ON question_record(subject_id);
CREATE INDEX IF NOT EXISTS idx_qr_created_at ON question_record(created_at DESC);
