package com.quasiris.qsc.test.generator;

import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    private List<Data> data = new ArrayList<>();

    private Long totalCount;

    public void init() {
        totalCount = 0L;
        for(Data d : data) {
            totalCount = totalCount + d.getCount();
        }


    }

    public void add(Data data) {
        this.data.add(data);
    }

    public String get(Long i) {
        Long current = i % totalCount;
        Long offset = 0L;
        for(Data d : data) {
            offset = offset + d.getCount();
            if(current < offset) {
                return d.getValue();
            }
        }
        return null;
    }

}
