package com.enation.app.shop.mobile.model;

import java.io.Serializable;

public class LiaomoCircleImage implements Serializable {

    private static final long serialVersionUID = 8909909529352424066L;

    private Long id;
    private Long circle_id;
    private String image;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCircle_id() {
        return circle_id;
    }
    
    public void setCircle_id(Long circle_id) {
        this.circle_id = circle_id;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
}
