package com.quasiris.qsc.qscspringfeeder.util;

import com.quasiris.qsc.qscspringfeeder.dto.QscFeedingDocument;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class TransformHelperTest {

    @Test
    void repairFile() throws IOException {
        List<QscFeedingDocument> docsResult = TransformHelper.transformRawParamsToHeaderPayloadStructure("jsons/ignore-by-git/example-not-visible-in-git-2.json");
        QscFeedingUtils.report(docsResult,"/mnt/report/big-file.json");
    }
}