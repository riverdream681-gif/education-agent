package com.yupi.yuaiagent.educationaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.yuaiagent.educationaiagent.entity.UserSubjectRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserSubjectRelationMapper extends BaseMapper<UserSubjectRelation> {

    @Select("SELECT COUNT(*) > 0 FROM user_subject_relation WHERE user_id = #{userId} AND subject_id = #{subjectId}")
    boolean existsByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);
}
