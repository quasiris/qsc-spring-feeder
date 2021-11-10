package com.quasiris.qsc.qscspringfeeder.util.category;

import com.quasiris.qsc.qscspringfeeder.dto.category.structured.Category;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.QscCategory;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.QscRootCategory;
import com.quasiris.qsc.qscspringfeeder.dto.category.tabed.DocumentLine;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabedCategoryParser {

    public static QscRootCategory readFromFile(File file) throws IOException {
        List<DocumentLine> documentLines = transformToTreeFromFile(file);
        List<Category> categories = transformToStrucureCategory(documentLines);
        List<QscCategory> resultQscCategories = categories.stream()
                .flatMap(c -> StructuredCategoryParser.toQscCategories(c).stream())
                .collect(Collectors.toList());
        QscRootCategory qscRootCategory = new QscRootCategory();
        qscRootCategory.setCategories(resultQscCategories);
        return qscRootCategory;
    }

    private static List<Category> transformToStrucureCategory(List<DocumentLine> documentLines) {
        List<Integer> rootIndexes = findRootIndexes(documentLines);
        return transformPart(documentLines, rootIndexes, 0);
    }

    private static List<Category> transformPart(List<DocumentLine> documentLines, List<Integer> rootIndexes, int level) {
        List<Category> resultList = new ArrayList<>();
        List<List<DocumentLine>> sublists = splitDocs(documentLines, rootIndexes);
        for (List<DocumentLine> sublist : sublists) {
            Category category = constructCategory(sublist, level);
            List<DocumentLine> remainItems = constructRemain(sublist);
            List<Integer> newRootIndexes = findRootIndexes(remainItems);
            category.setChildren(transformPart(remainItems, newRootIndexes, level + 1));
            resultList.add(category);
        }
        return resultList;
    }

    private static List<DocumentLine> constructRemain(List<DocumentLine> sublist) {
        List<DocumentLine> remain = sublist.subList(1, sublist.size());
        Optional<DocumentLine> min = remain.stream().min((Comparator.comparingInt(DocumentLine::getSpaceCount)));
        if (min.isEmpty()) {
            return new ArrayList<>();
        }
        int spaceCount = min.get().getSpaceCount();
        for (DocumentLine documentLine : remain) {
            documentLine.setSpaceCount(documentLine.getSpaceCount() - spaceCount);
        }
        return remain;
    }

    private static Category constructCategory(List<DocumentLine> documentLines, int level) {
        Category category = new Category();
        category.setId(UUID.randomUUID().toString());
        category.setName(documentLines.get(0).getBody());
        category.setLevel(level);
        return category;
    }

    private static List<List<DocumentLine>> splitDocs(List<DocumentLine> documentLines, List<Integer> rootIndexes) {
        List<List<DocumentLine>> sublists = new ArrayList<>();
        for (int i = 0; i < rootIndexes.size(); i++) {
            if (i < rootIndexes.size() - 1) {
                sublists.add(documentLines.subList(rootIndexes.get(i), rootIndexes.get(i + 1)));
            } else {
                sublists.add(documentLines.subList(rootIndexes.get(i), documentLines.size()));
            }
        }
        return sublists;
    }

    private static List<Integer> findRootIndexes(List<DocumentLine> documentLines) {
        List<Integer> rootIndexes = new ArrayList<>();
        for (int i = 0; i < documentLines.size(); i++) {
            if (documentLines.get(i).getSpaceCount() == 0) {
                rootIndexes.add(i);
            }
        }
        return rootIndexes;
    }

    public static List<DocumentLine> transformToTreeFromFile(File file) throws IOException {
        Pattern compile = Pattern.compile("(?<spaces>\\s*)(?<body>.+?)(\\s*)");
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            return stream.filter(e -> !StringUtils.isBlank(e))
                    .map(e -> {
                        Matcher matcher = compile.matcher(e);
                        if (matcher.matches()) {
                            return new DocumentLine(matcher.group("spaces").length(), matcher.group("body"));
                        }
                        return new DocumentLine(0, e);
                    })
                    .collect(Collectors.toList());
        }
    }
}
