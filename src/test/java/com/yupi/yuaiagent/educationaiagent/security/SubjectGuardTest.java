package com.yupi.yuaiagent.educationaiagent.security;

import com.yupi.yuaiagent.educationaiagent.mapper.UserSubjectRelationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubjectGuardTest {

    private final UserSubjectRelationMapper relationMapper = mock(UserSubjectRelationMapper.class);
    private final SubjectGuard guard = new SubjectGuard(relationMapper);

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccess_shouldReturnTrue_whenUserHasPermission() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1L, null));
        when(relationMapper.existsByUserIdAndSubjectId(1L, 100L)).thenReturn(true);

        assertTrue(guard.canAccess(100L));
    }

    @Test
    void canAccess_shouldReturnFalse_whenUserHasNoPermission() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(2L, null));
        when(relationMapper.existsByUserIdAndSubjectId(2L, 200L)).thenReturn(false);

        assertFalse(guard.canAccess(200L));
    }

    @Test
    void canAccess_shouldReturnFalse_whenNotAuthenticated() {
        assertFalse(guard.canAccess(100L));
    }
}
