package com.quasiris.qsc.qscspringfeeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@Slf4j
public class QscSpringFeederApplication implements ApplicationRunner {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();

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
    }
}