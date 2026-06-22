package com.yupi.yuaiagent.educationaiagent.config;

import com.yupi.yuaiagent.educationaiagent.document.KnowledgeDocLoader;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * PGVector 知识库向量存储配置
 */
@Configuration
public class KnowledgeVectorStoreConfig {

    @Resource
    private KnowledgeDocLoader knowledgeDocLoader;

    @Bean
    public VectorStore knowledgeVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("knowledge_vector_store")
                .maxDocumentBatchSize(10000)
                .build();

        List<Document> documents = knowledgeDocLoader.loadMarkdowns();
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }

        return vectorStore;
    }
}
