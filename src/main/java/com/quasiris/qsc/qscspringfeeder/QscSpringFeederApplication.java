package com.quasiris.qsc.qscspringfeeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@Slf4j
public class QscSpringFeederApplication implements ApplicationRunner {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String X_QSC_TOKEN_HEADER_NAME = "X-QSC-Token";

    @Value("${app.url}")
    String url;
    @Value("${app.tenant}")
    String tenant;
    @Value("${app.feeding.code}")
    String feedingCode;
    @Value("${app.x-qsc-token}")
    String xQscToken;
    @Value("${app.file.path}")
    String filePath;

    public static void main(String[] args) {
        SpringApplication.run(QscSpringFeederApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        log.info("url = {}", url);
        log.info("tenant = {}", tenant);
        log.info("feedingCode = {}", feedingCode);

        List<QscFeedingDocument> docs = objectMapper.readValue(new ClassPathResource(filePath).getFile(),
                new TypeReference<>() {
                });
        System.out.println("docs = " + docs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(X_QSC_TOKEN_HEADER_NAME, xQscToken);
        HttpEntity<List<QscFeedingDocument>> request = new HttpEntity<>(docs, headers);

        String uri = String.format("%s/api/v1/data/bulk/qsc/%s/%s",
                url, tenant, feedingCode);
        log.debug("uri = {}", uri);
        JsonNode response = restTemplate.postForObject(uri,
                request, JsonNode.class);
        System.out.println("response = " + response);
    }
}