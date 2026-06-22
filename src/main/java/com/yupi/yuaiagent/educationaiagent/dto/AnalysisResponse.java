package com.yupi.yuaiagent.educationaiagent.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalysisResponse {
    private String knowledgePoint;
    private String errorAnalysis;
    private List<SimilarQuestion> similarQuestions;

    @Data
    @Builder
    public static class SimilarQuestion {
        private String content;
    }
}
