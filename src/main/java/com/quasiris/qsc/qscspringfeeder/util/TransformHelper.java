package com.quasiris.qsc.qscspringfeeder.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.Header;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TransformHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<QscFeedingDocument> transformRawParamsToHeaderPayloadStructure(String srcFile) throws IOException {
        List<LinkedHashMap<String, Object>> data = objectMapper.readValue(new ClassPathResource(srcFile).getFile(), new TypeReference<>() {
        });
        List<QscFeedingDocument> resultDocs = new ArrayList<>();
        for (LinkedHashMap<String, Object> feedingEntry : data) {
            resultDocs.add(repairEntry(feedingEntry));
        }
        return resultDocs;
    }

    private static QscFeedingDocument repairEntry(LinkedHashMap<String, Object> feedingEntry) {
        Object id = feedingEntry.get("id");
        if (!(id instanceof String)) {
            throw new IllegalArgumentException("id has wrong type");
        }
        QscFeedingDocument newDocument = new QscFeedingDocument();
        newDocument.setHeader(new Header((String) id, "update"));
        feedingEntry.remove("id");
        newDocument.setPayload(feedingEntry);
        return newDocument;
    }


}
