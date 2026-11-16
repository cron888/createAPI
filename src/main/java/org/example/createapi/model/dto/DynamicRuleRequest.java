package org.example.createapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DynamicRuleRequest {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_text")
    private String productText;

    private DynamicRuleQueryDto[] queries;

    public DynamicRuleRequest() {}

    public DynamicRuleRequest(String productName, String productId, String productText, DynamicRuleQueryDto[] queries) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.queries = queries;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public DynamicRuleQueryDto[] getQueries() {
        return queries;
    }

    public void setQueries(DynamicRuleQueryDto[] queries) {
        this.queries = queries;
    }
}
