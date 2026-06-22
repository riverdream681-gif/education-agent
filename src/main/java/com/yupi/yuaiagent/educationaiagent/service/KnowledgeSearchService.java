package com.yupi.yuaiagent.educationaiagent.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识点检索服务
 */
@Service
public class KnowledgeSearchService {

    private final VectorStore vectorStore;

    public KnowledgeSearchService(VectorStore knowledgeVectorStore) {
        this.vectorStore = knowledgeVectorStore;
    }

    /**
     * 根据关键词检索知识点
     */
    public List<Document> search(String keyword, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(keyword)
                .topK(topK)
                .similarityThreshold(0.7)
                .build();
        return vectorStore.similaritySearch(request);
    }

    /**
     * 按学科过滤检索
     */
    public List<Document> searchBySubject(String keyword, String subject, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(keyword)
                .topK(topK)
                .similarityThreshold(0.7)
                .filterExpression("subject == '" + subject + "'")
                .build();
        return vectorStore.similaritySearch(request);
    }
}
