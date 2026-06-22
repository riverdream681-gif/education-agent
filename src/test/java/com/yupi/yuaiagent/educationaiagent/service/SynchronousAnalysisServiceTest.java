package com.yupi.yuaiagent.educationaiagent.service;

import com.yupi.yuaiagent.educationaiagent.dto.AnalysisRequest;
import com.yupi.yuaiagent.educationaiagent.dto.AnalysisResponse;
import com.yupi.yuaiagent.educationaiagent.mapper.QuestionRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SynchronousAnalysisServiceTest {

    @Mock private KnowledgeSearchService knowledgeService;
    @Mock private ChatClient.Builder chatClientBuilder;
    @Mock(answer = RETURNS_DEEP_STUBS) private ChatClient chatClient;
    @Mock private QuestionRecordMapper questionRecordMapper;

    private SynchronousAnalysisService service;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        service = new SynchronousAnalysisService(knowledgeService, chatClientBuilder, questionRecordMapper);
    }

    @Test
    void analyze_shouldReturnAnalysisResult() {
        Document doc = new Document("导数的几何意义...", Map.of("subject", "高中数学"));
        when(knowledgeService.search("求切线方程", 3)).thenReturn(List.of(doc));

        when(chatClient.prompt().user(anyString()).call().content()).thenReturn("""
                {"knowledgePoint":"导数的几何意义","errorAnalysis":"需要先求导再代入","similarQuestions":[{"content":"已知 f(x)=x³-3x 求切线"}]}""");

        when(questionRecordMapper.insert(any(com.yupi.yuaiagent.educationaiagent.entity.QuestionRecord.class))).thenReturn(1);

        AnalysisRequest request = new AnalysisRequest();
        request.setQuestion("求切线方程");
        request.setSubjectId(1L);
        AnalysisResponse result = service.analyze(request);

        assertEquals("导数的几何意义", result.getKnowledgePoint());
        assertTrue(result.getErrorAnalysis().contains("求导"));
        assertEquals(1, result.getSimilarQuestions().size());

        ArgumentCaptor<com.yupi.yuaiagent.educationaiagent.entity.QuestionRecord> c =
                ArgumentCaptor.forClass(com.yupi.yuaiagent.educationaiagent.entity.QuestionRecord.class);
        verify(questionRecordMapper).insert(c.capture());
        assertEquals("求切线方程", c.getValue().getQuestionContent());
    }

    @Test
    void analyze_shouldReturnFallback_whenLlmReturnsInvalidJson() {
        Document doc = new Document("导数的几何意义...", Map.of("subject", "高中数学"));
        when(knowledgeService.search(anyString(), eq(3))).thenReturn(List.of(doc));

        when(chatClient.prompt().user(anyString()).call().content()).thenReturn("这是非 JSON 格式的回复");

        when(questionRecordMapper.insert(any(com.yupi.yuaiagent.educationaiagent.entity.QuestionRecord.class))).thenReturn(1);

        AnalysisRequest request = new AnalysisRequest();
        request.setQuestion("求切线方程");
        AnalysisResponse result = service.analyze(request);

        assertNotNull(result.getKnowledgePoint());
        assertTrue(result.getErrorAnalysis().contains("解析失败") || result.getErrorAnalysis().contains("不可用"));
    }
}

