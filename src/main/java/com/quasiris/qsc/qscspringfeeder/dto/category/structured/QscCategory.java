package com.quasiris.qsc.qscspringfeeder.dto.category.structured;

import lombok.Data;

import java.util.List;

@Data
public class QscCategory {
    private List<QscCategoryNode> category;
}
