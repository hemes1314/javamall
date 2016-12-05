package com.enation.app.b2b2c.core.model.Drools;

import java.io.Serializable;

public class Product implements Serializable {

    private static final long serialVersionUID = -7950018873262611050L;
    
    private String id;
    private String promoId;
    private Integer quantity;
    private Double price;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPromoId() {
        return promoId;
    }
    
    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    
}
