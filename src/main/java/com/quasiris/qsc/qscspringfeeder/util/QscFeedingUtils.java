package com.quasiris.qsc.qscspringfeeder.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class QscFeedingUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String X_QSC_TOKEN_HEADER_NAME = "X-QSC-Token";
    private static final String REMAIN_ITEMS_REPORT_PATH = "report/remain-items.json";
    private static final String PUSHED_ITEMS_REPORT_PATH = "report/pushed-items.json";
    public static final int RETRY_COUNT = 5;
    static Scanner scanner = new Scanner(System.in);

    public static List<Object> readDocumentsFromDirectory(Path path) throws IOException {
        List<File> files = Files.list(path)
                .map(Path::toFile)
                .collect(Collectors.toList());

        List<Object> result = new ArrayList<>();
        for (File file : files) {
            result.addAll(readDocumentsFromFile(file));
        }
        return result;
    }

    public static List<Object> readDocumentsFromFile(File file) throws IOException {
        return objectMapper.readValue(file, new TypeReference<>() {
        });
    }

    public static List<QscFeedingDocument> readQscDocumentsFromFile(File file) throws IOException {
        return objectMapper.readValue(file, new TypeReference<>() {
        });
    }

    public static List<JsonNode> postFeeds(List<Object> docs,
                                           String fullUrl, String xQscToken,
                                           int batchSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(X_QSC_TOKEN_HEADER_NAME, xQscToken);
        return requestWithBatch(docs, headers, fullUrl, batchSize);
    }

    private static List<JsonNode> requestWithBatch(List<Object> docs,
                                                   HttpHeaders headers,
                                                   String uri,
                                                   int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("Batch size is less than 0");
        }
        List<JsonNode> responses = new ArrayList<>();
        List<Object> docsToSend = new ArrayList<>(docs);
        try {
            while (!docsToSend.isEmpty()) {
                responses.add(sendBatch(docsToSend, headers, uri, batchSize));
            }
        } catch (Exception e) {
            log.error("error: ", e);
            tryReportPushedAndRemainData(responses, docsToSend);
            throw new RuntimeException("Problems when trying to push data", e);
        }
        return responses;
    }

    private static void tryReportPushedAndRemainData(List<JsonNode> responses, List<Object> docsToSend) {
        try {
            Reporter.report(responses, PUSHED_ITEMS_REPORT_PATH);
            Reporter.report(docsToSend, REMAIN_ITEMS_REPORT_PATH);
            log.error("Problems when trying to push data. Not pushed data collected in {}, pushed responses collected in {}", REMAIN_ITEMS_REPORT_PATH, PUSHED_ITEMS_REPORT_PATH);
        } catch (Exception ex) {
            log.info("We could not collect remain and pushed data");
        }
    }

    private static JsonNode sendBatch(List<Object> docs, HttpHeaders headers, String uri, int batchSize) {
        List<Object> docsToSend = docs.subList(0, Math.min(batchSize, docs.size()));
        List<Object> remainDocs = new ArrayList<>();
        if (docsToSend.size() != docs.size()) {
            remainDocs.addAll(docs.subList(batchSize, docs.size()));
        }
        JsonNode result = tryToSendBatch(headers, uri, docsToSend, remainDocs);
        docs.clear();
        docs.addAll(remainDocs);
        return result;
    }

    private static JsonNode tryToSendBatch(HttpHeaders headers, String uri, List<Object> docsToSend, List<Object> remainDocs) {
        int numberOfTrying = 0;
        while (numberOfTrying <= RETRY_COUNT) {
            numberOfTrying++;
            doYouWantToContinue(numberOfTrying);
            try {
                logData(docsToSend, remainDocs);
                long startTime = System.nanoTime();
                HttpEntity<List<Object>> request = new HttpEntity<>(docsToSend, headers);
                ObjectNode jsonNode = restTemplate.postForObject(uri, request, ObjectNode.class);
                long endTime = System.nanoTime();
                long durationMilliseconds = (endTime - startTime) / 1000000;
                log.info("Success!");
                log.info("durationMilliseconds = {}", durationMilliseconds);
                jsonNode.put("durationMilliseconds", durationMilliseconds);
                return jsonNode;
            } catch (Exception e) {
                log.error("e: ", e);
            }
        }
        log.error("Problems, count of not pushed documents = {}", docsToSend.size() + remainDocs.size());
        throw new RuntimeException(String.format("Could not send the data to the server, (RETRY_COUNT = %o)", RETRY_COUNT));
    }

    private static void logData(List<Object> docsToSend, List<Object> remainDocs) {
        log.info("docsToSend.size() = {}", docsToSend.size());
        log.info("remainDocs.size() = {}", remainDocs.size());
        System.out.println("Full count of not pushed documents = " + (docsToSend.size() + remainDocs.size()));
    }

    private static void doYouWantToContinue(int numberOfTrying) {
        if (numberOfTrying > 1) {
            System.out.println("\n\nDo you want to continue? Press 1 to stop, or any input to continue");
            String input = scanner.nextLine();
            if ("1".equals(input)) {
                throw new RuntimeException("Canceled by user");
            }
        }
    }

}
