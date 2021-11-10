package com.quasiris.qsc.qscspringfeeder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QscCategories {

   private List<QscCategory> category;

    /**
     * Getter for property 'category'.
     *
     * @return Value for property 'category'.
     */
    public List<QscCategory> getCategory() {
        return category;
    }

    /**
     * Setter for property 'category'.
     *
     * @param category Value to set for property 'category'.
     */
    public void setCategory(List<QscCategory> category) {
        this.category = category;
    }
}
