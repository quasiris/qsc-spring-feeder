package com.quasiris.qsc.qscspringfeeder.util.category;

import com.quasiris.qsc.qscspringfeeder.dto.category.DocumentLine;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CategoriesTransformationHelper {

    public static List<DocumentLine> transformTree(File file) throws IOException {
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
