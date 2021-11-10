package com.quasiris.qsc.qscspringfeeder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QscCategory {

    private String id;
    private String name;
    private Integer level;

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property 'level'.
     *
     * @return Value for property 'level'.
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Setter for property 'level'.
     *
     * @param level Value to set for property 'level'.
     */
    public void setLevel(Integer level) {
        this.level = level;
    }
}
