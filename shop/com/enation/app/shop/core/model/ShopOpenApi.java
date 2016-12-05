package com.enation.app.shop.core.model;

public class ShopOpenApi implements java.io.Serializable {
    private Double serviceLevelScore;
    private Double postSpeedScore;
    private Double describeMatchScore;
    private String shopId;

    public Double getServiceLevelScore() {
        return serviceLevelScore;
    }

    public void setServiceLevelScore(Double serviceLevelScore) {
        this.serviceLevelScore = serviceLevelScore;
    }

    public Double getPostSpeedScore() {
        return postSpeedScore;
    }

    public void setPostSpeedScore(Double postSpeedScore) {
        this.postSpeedScore = postSpeedScore;
    }

    public Double getDescribeMatchScore() {
        return describeMatchScore;
    }

    public void setDescribeMatchScore(Double describeMatchScore) {
        this.describeMatchScore = describeMatchScore;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}