package org.example.createapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DynamicRuleQueryDto {

    private String query;
    private String[] arguments;

    @JsonProperty("negate")
    private boolean negate;

    public DynamicRuleQueryDto() {}

    public DynamicRuleQueryDto(String query, String[] arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}
