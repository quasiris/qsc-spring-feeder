package com.quasiris.qsc.qscspringfeeder.dto.category.structured;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class QscRootCategory {
    private List<QscCategory> categories;
}
