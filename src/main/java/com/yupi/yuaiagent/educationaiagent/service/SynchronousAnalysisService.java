package com.yupi.yuaiagent.educationaiagent.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.yuaiagent.educationaiagent.dto.AnalysisRequest;
import com.yupi.yuaiagent.educationaiagent.dto.AnalysisResponse;
import com.yupi.yuaiagent.educationaiagent.entity.QuestionRecord;
import com.yupi.yuaiagent.educationaiagent.mapper.QuestionRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 同步错题分析服务
 * 流程: 文本输入 → RAG 检索 → Prompt 拼接 → LLM 调用 → 结果解析 → 落库
 */
@Service
@Slf4j
public class SynchronousAnalysisService {

    private final KnowledgeSearchService knowledgeService;
    private final ChatClient chatClient;
    private final QuestionRecordMapper questionRecordMapper;

    public SynchronousAnalysisService(KnowledgeSearchService knowledgeService,
                                       ChatClient.Builder chatClientBuilder,
                                       QuestionRecordMapper questionRecordMapper) {
        this.knowledgeService = knowledgeService;
        this.chatClient = chatClientBuilder.build();
        this.questionRecordMapper = questionRecordMapper;
    }

    public AnalysisResponse analyze(AnalysisRequest request) {
        List<Document> knowledgeDocs = knowledgeService.search(request.getQuestion(), 3);
        String knowledgeContext = knowledgeDocs.stream()
                .map(d -> "### " + d.getMetadata().getOrDefault("subject", "") + "\n" + d.getText())
                .collect(Collectors.joining("\n\n"));

        String prompt = buildPrompt(request.getQuestion(), knowledgeContext);

        String llmResponse;
        try {
            llmResponse = chatClient.prompt().user(prompt).call().content();
            log.info("LLM 响应长度: {}", llmResponse != null ? llmResponse.length() : 0);
        } catch (Exception e) {
            log.error("LLM 调用失败", e);
            return buildFallbackResponse(knowledgeDocs);
        }

        AnalysisResponse result = parseLlmResponse(llmResponse, knowledgeDocs);
        saveRecord(request, result, llmResponse);
        return result;
    }

    private String buildPrompt(String question, String knowledgeContext) {
        return """
                你是一个高中学科错题分析专家。请根据以下知识点内容，分析学生的错题。
                
                ## 相关知识点
                %s
                
                ## 学生错题
                %s
                
                请以 JSON 格式返回分析结果：
                {
                  "knowledgePoint": "相关的知识点名称",
                  "errorAnalysis": "对错因的详细分析（200字以内）",
                  "similarQuestions": [
                    { "content": "一道同类练习题" }
                  ]
                }
                
                只返回 JSON，不要包含其他文字。
                """.formatted(knowledgeContext, question);
    }

    private AnalysisResponse parseLlmResponse(String llmResponse, List<Document> knowledgeDocs) {
        try {
            String json = llmResponse;
            if (llmResponse.contains("```json")) {
                int start = llmResponse.indexOf("```json") + 7;
                int end = llmResponse.indexOf("```", start);
                if (end > start) json = llmResponse.substring(start, end).trim();
            } else if (llmResponse.contains("```")) {
                int start = llmResponse.indexOf("```") + 3;
                int end = llmResponse.indexOf("```", start);
                if (end > start) json = llmResponse.substring(start, end).trim();
            }

            JSONObject obj = JSONUtil.parseObj(json);
            String knowledgePoint = obj.getStr("knowledgePoint", "未知知识点");
            String errorAnalysis = obj.getStr("errorAnalysis", "分析生成失败");

            List<AnalysisResponse.SimilarQuestion> similar = Collections.emptyList();
            if (obj.containsKey("similarQuestions")) {
                similar = obj.getJSONArray("similarQuestions").stream()
                        .map(o -> {
                            JSONObject sq = (JSONObject) o;
                            return AnalysisResponse.SimilarQuestion.builder()
                                    .content(sq.getStr("content", ""))
                                    .build();
                        })
                        .collect(Collectors.toList());
            }

            return AnalysisResponse.builder()
                    .knowledgePoint(knowledgePoint)
                    .errorAnalysis(errorAnalysis)
                    .similarQuestions(similar)
                    .build();

        } catch (Exception e) {
            log.warn("JSON 解析失败，返回降级结果", e);
            return buildFallbackResponse(knowledgeDocs);
        }
    }

    private AnalysisResponse buildFallbackResponse(List<Document> knowledgeDocs) {
        String knowledgeNames = knowledgeDocs.stream()
                .map(d -> (String) d.getMetadata().getOrDefault("subject", "未知"))
                .distinct()
                .collect(Collectors.joining("、"));

        return AnalysisResponse.builder()
                .knowledgePoint(knowledgeDocs.isEmpty() ? "未匹配到知识点" : knowledgeNames)
                .errorAnalysis("AI 分析服务暂时不可用，解析失败，请稍后重试")
                .similarQuestions(Collections.emptyList())
                .build();
    }

    private void saveRecord(AnalysisRequest request, AnalysisResponse result, String rawResponse) {
        try {
            QuestionRecord record = new QuestionRecord();
            record.setUserId(null);
            record.setSubjectId(request.getSubjectId());
            record.setQuestionContent(request.getQuestion());
            record.setAnalysisResult(rawResponse);
            record.setKnowledgePoint(result.getKnowledgePoint());
            record.setIsCorrect(null);
            record.setCreatedAt(LocalDateTime.now());
            questionRecordMapper.insert(record);
        } catch (Exception e) {
            log.error("保存分析记录失败", e);
        }
    }
}
