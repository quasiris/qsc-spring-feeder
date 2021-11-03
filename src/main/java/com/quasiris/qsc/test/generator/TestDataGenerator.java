package com.quasiris.qsc.test.generator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<String> getMultiple(Long i, int min, int max) {
        Set<String> ret = new LinkedHashSet<>();
        int valueCount = getRandom(min, max);
        if(valueCount >= data.size()) {
            return data.stream().map(d -> d.getValue()).collect(Collectors.toList());
        }

        while(ret.size() < valueCount) {
            int index = getRandom(0, data.size());
            ret.add(data.get(index).getValue());
        }
        return new ArrayList<>(ret);
    }


    int getRandom(int min, int max) {
        Random r = new Random();
        int result = r.nextInt(max-min) + min;
        return result;
    }


}
