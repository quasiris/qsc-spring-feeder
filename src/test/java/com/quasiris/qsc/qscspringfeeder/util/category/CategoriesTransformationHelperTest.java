package com.quasiris.qsc.qscspringfeeder.util.category;

import com.quasiris.qsc.qscspringfeeder.dto.category.DocumentLine;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

class CategoriesTransformationHelperTest {


    @Test
    void transformTree() throws IOException {
        List<DocumentLine> documentLines = CategoriesTransformationHelper.transformTree(new ClassPathResource("/jsons/categories/example-categories.txt").getFile());
        System.out.println("documentLines = " + documentLines);
    }

}