package com.yupi.yuaiagent.educationaiagent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)

@SpringBootTest
class KnowledgeSearchServiceTest {

//    @Mock
    @Autowired
    @Qualifier("knowledgeVectorStore")
    private VectorStore vectorStore;

    private KnowledgeSearchService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeSearchService(vectorStore);
    }

    @Test
    void search_shouldReturnDocuments() {
        Document doc = new Document("导数的几何意义...", Map.of("subject", "高中数学", "filename", "test.md"));
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(doc));

        List<Document> results = service.search("导数", 3);

        assertEquals(1, results.size());
        assertEquals("高中数学", results.get(0).getMetadata().get("subject"));
    }

    @Test
    void search_shouldReturnEmptyList_whenNoResults() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());

        List<Document> results = service.search("不存在关键词", 3);

        assertTrue(results.isEmpty());
    }

    @Test
    void searchBySubject_shouldFilterBySubject() {
        Document doc = new Document("导数的几何意义...", Map.of("subject", "高中数学"));
//        when(vectorStore.similaritySearch(any(SearchRequest.class)))
//                .thenReturn(List.of(doc));

        List<Document> results = service.searchBySubject("导数", "高中数学", 3);

        assertEquals(1, results.size());
        assertEquals("高中数学", results.get(0).getMetadata().get("subject"));
    }
}
