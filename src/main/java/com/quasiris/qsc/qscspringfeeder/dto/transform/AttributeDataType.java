package com.quasiris.qsc.qscspringfeeder.dto.transform;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@Getter
public enum AttributeDataType {

    STRING("t", "string"),
    NUMBER("n", "double"),
    BOOLEAN("b", "boolean");

    private final String key;
    private final String value;

    public static @Nullable
    AttributeDataType findByKey(String key) {
        for (AttributeDataType value : values()) {
            if (value.key.equals(key)) {
                return value;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
