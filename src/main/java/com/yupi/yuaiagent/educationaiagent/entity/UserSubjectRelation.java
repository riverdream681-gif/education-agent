package com.yupi.yuaiagent.educationaiagent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_subject_relation")
public class UserSubjectRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long subjectId;
}
