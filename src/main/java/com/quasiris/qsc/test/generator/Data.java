package com.quasiris.qsc.test.generator;

public class Data {

    private String value;
    private Long count;

    public Data(String value, Long count) {
        this.value = value;
        this.count = count;
    }

    /**
     * Getter for property 'value'.
     *
     * @return Value for property 'value'.
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for property 'value'.
     *
     * @param value Value to set for property 'value'.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Getter for property 'count'.
     *
     * @return Value for property 'count'.
     */
    public Long getCount() {
        return count;
    }

    /**
     * Setter for property 'count'.
     *
     * @param count Value to set for property 'count'.
     */
    public void setCount(Long count) {
        this.count = count;
    }
}
