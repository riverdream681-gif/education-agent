package com.yupi.yuaiagent.educationaiagent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question_record")
public class QuestionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** Phase 1 允许 NULL */
    private Long userId;

    private Long subjectId;
    private String questionContent;
    private String answerContent;
    private String analysisResult;
    private String knowledgePoint;
    private Boolean isCorrect;
    private LocalDateTime createdAt;
}
