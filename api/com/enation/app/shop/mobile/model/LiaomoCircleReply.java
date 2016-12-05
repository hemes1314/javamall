package com.enation.app.shop.mobile.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class LiaomoCircleReply implements Serializable {

    private static final long serialVersionUID = -5187818085612267543L;

    private Long id;
    private Long circle_id;
    private String reply;
    
    @PrimaryKeyField
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
    
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
    
}
