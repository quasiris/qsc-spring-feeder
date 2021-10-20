package com.quasiris.qsc.qscspringfeeder.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QscFeedingUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String X_QSC_TOKEN_HEADER_NAME = "X-QSC-Token";

    public static List<QscFeedingDocument> readDocumentsFromFile(String filePath) throws IOException {
        return objectMapper.readValue(new ClassPathResource(filePath).getFile(),
                new TypeReference<>() {
                });
    }

    public static String constructUrl(String prefix, String tenant, String feedingCode) {
        return String.format("%s/api/v1/data/bulk/qsc/%s/%s", prefix, tenant, feedingCode);
    }

    public static List<JsonNode> postFeeds(List<QscFeedingDocument> docs,
                                           String xQscToken,
                                           String urlPrefix,
                                           String tenant,
                                           String feedingCode,
                                           int batchSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(X_QSC_TOKEN_HEADER_NAME, xQscToken);
        String uri = constructUrl(urlPrefix, tenant, feedingCode);
        return requestWithBatch(docs, headers, uri, batchSize);
    }

    private static List<JsonNode> requestWithBatch(List<QscFeedingDocument> docs,
                                                   HttpHeaders headers,
                                                   String uri,
                                                   int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("Batch size is less than 0");
        }
        List<JsonNode> responses = new ArrayList<>();
        List<QscFeedingDocument> docsToSend = new ArrayList<>(docs);
        while (!docsToSend.isEmpty()) {
            responses.add(sendBatch(docsToSend, headers, uri, batchSize));
        }
        return responses;
    }

    private static JsonNode sendBatch(List<QscFeedingDocument> docs, HttpHeaders headers, String uri, int batchSize) {
        List<QscFeedingDocument> docsToSend = docs.subList(0, Math.min(batchSize, docs.size()));
        List<QscFeedingDocument> remainDocs = new ArrayList<>();
        if (docsToSend.size() != docs.size()) {
            remainDocs.addAll(docs.subList(batchSize, docs.size()));
        }
        log.debug("docsToSend.size() = {}", docsToSend.size());
        log.debug("remainDocs.size() = {}", remainDocs.size());
        HttpEntity<List<QscFeedingDocument>> request = new HttpEntity<>(docsToSend, headers);
        JsonNode result = restTemplate.postForObject(uri, request, JsonNode.class);
        docs.clear();
        docs.addAll(remainDocs);
        return result;
    }

    public static void report(List<JsonNode> responses, String reportPath) throws IOException {
        try {
            if (reportPath != null && !reportPath.trim().isEmpty()) {
                File resultFile = new File(reportPath);
                resultFile.getParentFile().mkdirs();
                if (!resultFile.exists()) {
                    resultFile.createNewFile();
                }
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(resultFile, responses);
                log.info("Reported to file (file = {})", resultFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Not reported", e);
        }
    }
}
