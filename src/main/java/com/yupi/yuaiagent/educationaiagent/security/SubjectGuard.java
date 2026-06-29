package com.yupi.yuaiagent.educationaiagent.security;

import com.yupi.yuaiagent.educationaiagent.mapper.UserSubjectRelationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 学科权限守卫 — @PreAuthorize("@subjectGuard.canAccess(#subjectId)") 调用
 */
@Component("subjectGuard")
@RequiredArgsConstructor
public class SubjectGuard {

    private final UserSubjectRelationMapper relationMapper;

    public boolean canAccess(Long subjectId) {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof Long userId) {
            return relationMapper.existsByUserIdAndSubjectId(userId, subjectId);
        }
        return false;
    }
}
