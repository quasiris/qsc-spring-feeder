package com.quasiris.qsc.qscspringfeeder;

import com.fasterxml.jackson.databind.JsonNode;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import com.quasiris.qsc.qscspringfeeder.util.QscFeedingUtils;
import com.quasiris.qsc.qscspringfeeder.util.Reporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
@Slf4j
public class QscSpringFeederApplication implements ApplicationRunner {

    private static final String LOG_REPORT_FILE_PATH = "report/log-report.json";

    @Value("${app.url}")
    String url;
    @Value("${app.x-qsc-token}")
    String xQscToken;
    @Value("${app.file.path}")
    String filePath;
    @Value("${app.file.directory}")
    String directory;
    @Value("${app.report.path}")
    String reportPath;
    @Value("${app.batch.size}")
    int batchSize;
    @Value("${app.continue.flag}")
    boolean continuePreviousWork;
    @Value("${app.continue.path}")
    String continuePath;


    public static void main(String[] args) {
        SpringApplication.run(QscSpringFeederApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        assertConfiguration();

        log.debug("url = {}", url);
        log.debug("batchSize = {}", batchSize);
        List<Object> docs;
        if (continuePreviousWork) {
            docs = QscFeedingUtils.readDocumentsFromFile(new File(continuePath));
        } else {
            docs = QscFeedingUtils.readDocumentsFromDirectory(Path.of(directory));
//            docs = QscFeedingUtils.readDocumentsFromFile(new ClassPathResource(filePath).getFile());
//            docs = TransformHelper.transformRawParamsToHeaderPayloadStructure(new ClassPathResource(filePath).getFile());
        }

        log.debug("docs.size() = {}", docs.size());

        Reporter.report(docs, LOG_REPORT_FILE_PATH);

        List<JsonNode> responses = QscFeedingUtils.postFeeds
                (docs, url, xQscToken, batchSize);
        Reporter.report(responses, reportPath);
        log.info("Push feeding successfully completed, count of requests = {}", responses.size());
    }

    private void assertConfiguration() {
        assert !url.isEmpty();
        assert !xQscToken.isEmpty();
        assert !filePath.isEmpty();
    }

}