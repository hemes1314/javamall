package com.enation.app.shop.mobile.model;

import java.io.Serializable;

public class LiaomoCircle implements Serializable {

    private static final long serialVersionUID = -945974698669917883L;

    private Long id;
    private Long member_id;
    private String content;
    private Integer created_on;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMember_id() {
        return member_id;
    }
    
    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getCreated_on() {
        return created_on;
    }
    
    public void setCreated_on(Integer created_on) {
        this.created_on = created_on;
    }
    
}
