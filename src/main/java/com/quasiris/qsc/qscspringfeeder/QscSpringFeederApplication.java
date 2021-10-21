package com.quasiris.qsc.qscspringfeeder;

import com.fasterxml.jackson.databind.JsonNode;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import com.quasiris.qsc.qscspringfeeder.util.QscFeedingUtils;
import com.quasiris.qsc.qscspringfeeder.util.TransformHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@Slf4j
public class QscSpringFeederApplication implements ApplicationRunner {

    @Value("${app.url}")
    String urlPrefix;
    @Value("${app.tenant}")
    String tenant;
    @Value("${app.feeding.code}")
    String feedingCode;
    @Value("${app.x-qsc-token}")
    String xQscToken;
    @Value("${app.file.path}")
    String filePath;
    @Value("${app.report.path}")
    String reportPath;
    @Value("${app.batch.size}")
    int batchSize;

    public static void main(String[] args) {
        SpringApplication.run(QscSpringFeederApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        assertConfiguration();

        log.info("url = {}", urlPrefix);
        log.info("tenant = {}", tenant);
        log.info("feedingCode = {}", feedingCode);
        log.debug("batchSize = {}", batchSize);

//        List<QscFeedingDocument> docs = QscFeedingUtils.readDocumentsFromFile(filePath);
        List<QscFeedingDocument> docs = TransformHelper.transformRawParamsToHeaderPayloadStructure(filePath);
        log.debug("docs.size() = {}", docs.size());

        QscFeedingUtils.report(docs, "report/log-report.json");

//        List<JsonNode> responses = QscFeedingUtils.postFeeds
//                (docs, xQscToken, urlPrefix, tenant, feedingCode, batchSize);
//        QscFeedingUtils.report(responses, reportPath);
//        log.debug("responses = {}", responses);
    }

    private void assertConfiguration() {
        assert !urlPrefix.isEmpty();
        assert !tenant.isEmpty();
        assert !feedingCode.isEmpty();
        assert !xQscToken.isEmpty();
        assert !filePath.isEmpty();
    }

}