package com.quasiris.qsc.qscspringfeeder.dto.category.structured;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Category {
    private String id;
    private String name;
    private Integer level;
    private Integer position;
    List<Category> children = new ArrayList<>();
    @JsonIgnore
    private transient Category parent;
}
