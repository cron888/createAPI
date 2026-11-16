package org.example.createapi.model.dto;

import java.util.List;

public class RecommendationResponse {

    private String userId;
    private List<RecommendationItem> recommendations;

    public RecommendationResponse(String userId, List<RecommendationItem> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<RecommendationItem> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }

    public static class RecommendationItem {
        private String productId;
        private String productText;

        public RecommendationItem(String productId, String productText) {
            this.productId = productId;
            this.productText = productText;
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
    }
}
