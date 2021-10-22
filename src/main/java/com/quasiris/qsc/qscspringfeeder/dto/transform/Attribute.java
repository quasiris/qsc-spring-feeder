package com.quasiris.qsc.qscspringfeeder.dto.transform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute {
    private String id;
    private String name;
    private AttributeDataType dataType;
    private List<?> values;

    public Attribute(String id, String name, AttributeDataType dataType) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
    }
}
