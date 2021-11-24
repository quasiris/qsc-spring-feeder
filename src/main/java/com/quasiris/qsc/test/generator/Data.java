package com.quasiris.qsc.test.generator;

public class Data<T> {

    private T value;
    private Long count;

    public Data(T value, Long count) {
        this.value = value;
        this.count = count;
    }

    /**
     * Getter for property 'value'.
     *
     * @return Value for property 'value'.
     */
    public T getValue() {
        return value;
    }

    /**
     * Setter for property 'value'.
     *
     * @param value Value to set for property 'value'.
     */
    public void setValue(T value) {
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
