package com.yupi.yuaiagent.educationaiagent.document;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学科知识点文档加载器
 */
@Component
@Slf4j
public class KnowledgeDocLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public KnowledgeDocLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 document/ 下所有学科 Markdown 文档
     * 每个知识点由 --- 分隔，自动提取 subject 元数据
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;

                String subject = extractSubject(filename);

                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("subject", subject)
                        .withAdditionalMetadata("source", "knowledge_base")
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                List<Document> docs = reader.get();

                docs.forEach(d -> {
                    if (!d.getMetadata().containsKey("subject") || d.getMetadata().get("subject") == null) {
                        d.getMetadata().put("subject", subject);
                    }
                });

                docs = docs.stream()
                        .filter(d -> d.getText() != null && !d.getText().isBlank())
                        .collect(Collectors.toList());

                allDocuments.addAll(docs);
                log.info("加载文档: {}, 知识点数: {}", filename, docs.size());
            }
        } catch (IOException e) {
            log.error("学科 Markdown 文档加载失败", e);
        }
        log.info("知识库总计加载 {} 个知识点", allDocuments.size());
        return allDocuments;
    }

    /**
     * 从文件名提取学科名称
     * 如 "高中数学 - 函数与导数.md" -> "高中数学"
     */
    private String extractSubject(String filename) {
        if (filename.contains("恋爱")) return null;

        String name = filename.replace(".md", "");
        int idx = name.indexOf(" - ");
        if (idx > 0) return name.substring(0, idx).trim();
        idx = name.indexOf("-");
        if (idx > 0) return name.substring(0, idx).trim();
        return name.trim();
    }
}
