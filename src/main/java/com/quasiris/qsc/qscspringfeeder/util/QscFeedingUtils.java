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
    private static final String REMAIN_ITEMS_REPORT_PATH = "report/remain-items.json";
    private static final String PUSHED_ITEMS_REPORT_PATH = "report/pushed-items.json";

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
        try {
            while (!docsToSend.isEmpty()) {
                responses.add(sendBatch(docsToSend, headers, uri, batchSize));
            }
        } catch (Exception e) {
            tryReportPushedAndRemainData(responses);
            throw new RuntimeException("Problems when trying to push data");
        }
        return responses;
    }

    private static void tryReportPushedAndRemainData(List<JsonNode> responses) {
        try {
            report(responses, PUSHED_ITEMS_REPORT_PATH);
            report(responses, REMAIN_ITEMS_REPORT_PATH);
            log.error("Problems when trying to push data. Not pushed data collected in {}, pushed responses collected in {}", REMAIN_ITEMS_REPORT_PATH, PUSHED_ITEMS_REPORT_PATH);
        } catch (Exception ex) {
            log.info("We could not collect remain and pushed data");
        }
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

    public static void report(List<?> responses, String reportPath) throws IOException {
        try {
            if (reportPath != null && !reportPath.trim().isEmpty()) {
                File resultFile = new File(reportPath);
                createFileIfNotExists(resultFile);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(resultFile, responses);
                log.info("Reported to file (file = {})", resultFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Not reported", e);
        }
    }

    private static void createFileIfNotExists(File resultFile) throws IOException {
        File parentFile = resultFile.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }
    }
}
