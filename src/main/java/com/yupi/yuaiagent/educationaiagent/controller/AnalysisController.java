package com.yupi.yuaiagent.educationaiagent.controller;

import com.yupi.yuaiagent.educationaiagent.dto.AnalysisRequest;
import com.yupi.yuaiagent.educationaiagent.dto.AnalysisResponse;
import com.yupi.yuaiagent.educationaiagent.service.KnowledgeSearchService;
import com.yupi.yuaiagent.educationaiagent.service.SynchronousAnalysisService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class AnalysisController {

    private final SynchronousAnalysisService analysisService;
    private final KnowledgeSearchService knowledgeService;

    public AnalysisController(SynchronousAnalysisService analysisService,
                               KnowledgeSearchService knowledgeService) {
        this.analysisService = analysisService;
        this.knowledgeService = knowledgeService;
    }

    @PostMapping("/analysis/sync")
    public Map<String, Object> syncAnalyze(@RequestBody AnalysisRequest request) {
        AnalysisResponse result = analysisService.analyze(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("data", result);
        return response;
    }

    @GetMapping("/knowledge/search")
    public Map<String, Object> searchKnowledge(
            @RequestParam String keyword,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false, defaultValue = "5") int topK) {

        List<Document> results;
        if (subject != null && !subject.isBlank()) {
            results = knowledgeService.searchBySubject(keyword, subject, topK);
        } else {
            results = knowledgeService.search(keyword, topK);
        }

        List<Map<String, Object>> items = results.stream().map(doc -> {
            Map<String, Object> item = new HashMap<>();
            item.put("content", doc.getText());
            item.put("subject", doc.getMetadata().getOrDefault("subject", ""));
            item.put("score", doc.getMetadata().getOrDefault("score", 0));
            item.put("id", doc.getId());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("data", items);
        return response;
    }
}
