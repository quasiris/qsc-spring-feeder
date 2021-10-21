package com.quasiris.qsc.qscspringfeeder.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.Header;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import com.quasiris.qsc.qscspringfeeder.dto.transform.Attribute;
import com.quasiris.qsc.qscspringfeeder.dto.transform.AttributeDataType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ACTION_UPDATE = "update";
    private static final String PARAM_ATTRIBUTES = "attributes";
    private static final String PARAM_ID = "id";

    private static final String REGEX_NAME_GROUP = "name";
    private static final String REGEX_TYPE_GROUP = "type";
    private static final Pattern attributePattern = Pattern.compile("attr_(?<type>[btn])_(?<name>.+)");


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
        Object id = feedingEntry.get(PARAM_ID);
        if (!(id instanceof String)) {
            throw new IllegalArgumentException("id has wrong type");
        }
        QscFeedingDocument newDocument = new QscFeedingDocument();
        newDocument.setHeader(new Header((String) id, ACTION_UPDATE));
        addAttributesIfExists(feedingEntry);
        newDocument.setPayload(feedingEntry);
        return newDocument;
    }

    private static void addAttributesIfExists(LinkedHashMap<String, Object> feedingEntry) {
        List<Attribute> attributes = new ArrayList<>();
        addValidAttributes(feedingEntry, attributes);
        if (!attributes.isEmpty()) {
            feedingEntry.put(PARAM_ATTRIBUTES, attributes);
        }
    }

    private static void addValidAttributes(LinkedHashMap<String, Object> feedingEntry, List<Attribute> attributes) {
        for (String key : feedingEntry.keySet()) {
            Attribute attribute = convertToAttributeIfPossible(key, feedingEntry);
            if (attribute != null) {
                feedingEntry.remove(key);
                attributes.add(attribute);
            }
        }
    }

    private static @Nullable
    Attribute convertToAttributeIfPossible(String key, LinkedHashMap<String, Object> feedingMap) {
        Matcher matcher = attributePattern.matcher(key);
        if (matcher.matches()) {
            String attrName = matcher.group(REGEX_NAME_GROUP);
            String attrTypeKey = matcher.group(REGEX_TYPE_GROUP);
            AttributeDataType attributeDataType = AttributeDataType.findByKey(attrTypeKey);
            if (attributeDataType != null) {
                return new Attribute(null, attrName, attributeDataType, Collections.singletonList(feedingMap.get(key)));
            }
        }
        return null;
    }


}
