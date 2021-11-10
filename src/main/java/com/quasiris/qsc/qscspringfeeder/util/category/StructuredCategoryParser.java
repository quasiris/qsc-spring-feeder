package com.quasiris.qsc.qscspringfeeder.util.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.Category;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.QscCategory;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.QscCategoryNode;
import com.quasiris.qsc.qscspringfeeder.dto.category.structured.QscRootCategory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StructuredCategoryParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static QscRootCategory parseFromFile(File file) throws IOException {
        Category category = objectMapper.readValue(file, Category.class);
        return transform(category);
    }

    public static QscRootCategory transform(Category category) {
        List<QscCategory> categories = toQscCategories(category);
        System.out.println("categories.size() = " + categories.size());
        QscRootCategory qscRootCategory = new QscRootCategory();
        qscRootCategory.setCategories(categories);
        return qscRootCategory;
    }


    public static List<QscCategory> toQscCategories(Category category) {
        List<Category> leafs = findLeafsAndPutParent(category, new ArrayList<>());
        List<QscCategory> fstChildren = new ArrayList<>();
        for (Category leaf : leafs) {
            QscCategory fstChild = new QscCategory();
            List<QscCategoryNode> stringOfCategories = calcStringOfCategories(leaf, new ArrayList<>());
            fstChild.setCategory(stringOfCategories);
            fstChildren.add(fstChild);
        }
        for (QscCategory newCat : fstChildren) {
            newCat.getCategory().sort(Comparator.comparing(QscCategoryNode::getLevel));
        }
        return fstChildren;
    }

    private static List<QscCategoryNode> calcStringOfCategories(Category leaf, List<QscCategoryNode> categories) {
        categories.add(mapToSecCategory(leaf));
        if (leaf.getParent() != null) {
            return calcStringOfCategories(leaf.getParent(), categories);
        }
        return categories;
    }


    private static QscCategoryNode mapToSecCategory(Category category) {
        QscCategoryNode current = new QscCategoryNode();
        current.setId(category.getId());
        current.setName(category.getName());
        current.setLevel(category.getLevel());
        return current;
    }

    private static List<Category> findLeafsAndPutParent(Category category, List<Category> categories) {
        if (category.getChildren().isEmpty()) {
            categories.add(category);
            return categories;
        }
        for (Category child : category.getChildren()) {
            child.setParent(category);
            findLeafsAndPutParent(child, categories);
        }
        return categories;
    }
}
