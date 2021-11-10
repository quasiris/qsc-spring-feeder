package com.quasiris.qsc.qscspringfeeder.util.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.QscRootCategory;
import com.quasiris.qsc.qscspringfeeder.dto.category.tabed.DocumentLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

class TabedCategoryParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void transformTree() throws IOException {
        List<DocumentLine> documentLines = TabedCategoryParser.transformToTreeFromFile(new ClassPathResource("/jsons/categories/example-categories.txt").getFile());
        System.out.println("documentLines = " + documentLines);
    }

    @Test
    void readFromFile() throws IOException {
        QscRootCategory qscRootCategory = TabedCategoryParser.readFromFile(new ClassPathResource("/jsons/categories/example-categories.txt").getFile());
//        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("result.json"), qscRootCategory);
        Assertions.assertEquals(4, qscRootCategory.getCategories().size());
    }
}