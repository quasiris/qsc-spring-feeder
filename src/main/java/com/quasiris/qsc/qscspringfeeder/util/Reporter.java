package com.quasiris.qsc.qscspringfeeder.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class Reporter {
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final RestTemplate restTemplate = new RestTemplate();

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
