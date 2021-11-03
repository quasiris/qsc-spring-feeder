package com.quasiris.qsc.qscspringfeeder.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.Header;
import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import com.quasiris.qsc.qscspringfeeder.dto.transform.Attribute;
import com.quasiris.qsc.qscspringfeeder.dto.transform.AttributeDataType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TransformHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ACTION_UPDATE = "update";
    private static final String PARAM_ATTRIBUTES = "attributes";
    private static final String PARAM_ID = "id";

    private static final String REGEX_NAME_GROUP = "name";
    private static final String REGEX_TYPE_GROUP = "type";
    private static final Pattern attributePattern = Pattern.compile("attr_(?<type>[btn])_(?<name>.+)");
    private static final Pattern splitPattern = Pattern.compile("\\s*,\\s*");


    public static List<QscFeedingDocument> transformRawParamsToHeaderPayloadStructure(File file) throws IOException {
        List<LinkedHashMap<String, Object>> data = objectMapper.readValue(file, new TypeReference<>() {
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
        for (String key : new HashSet<>(feedingEntry.keySet())) {
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
                Object value = feedingMap.get(key);
                Attribute attribute = new Attribute(key, attrName, attributeDataType);
                assignAttributeValues(attributeDataType, value, attribute);
                return attribute;
            }
        }
        return null;
    }

    private static void assignAttributeValues(AttributeDataType attributeDataType, Object value, Attribute attribute) {
        if (attributeDataType == AttributeDataType.STRING) {
            String stringValue = (String) value;
            List<String> values = Arrays.stream(splitPattern.split(stringValue))
                    .filter(s -> !StringUtils.isBlank(s))
                    .collect(Collectors.toList());
            attribute.setValues(values);
        } else {
            attribute.setValues(Collections.singletonList(value));
        }
    }


}
