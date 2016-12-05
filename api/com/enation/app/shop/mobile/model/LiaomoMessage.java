package com.enation.app.shop.mobile.model;

import java.io.Serializable;

public class LiaomoMessage implements Serializable {

    private static final long serialVersionUID = -318015763500061755L;
    
    private Integer id;
    private Long sender;
    private Integer content_type;
    private String content;
    private Long created_on;
    private Long group_id;
    private Long receiver;
    private String content_length;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }
    
    public void setSender(Long sender) {
        this.sender = sender;
    }
    
    public Integer getContent_type() {
        return content_type;
    }
    
    public void setContent_type(Integer content_type) {
        this.content_type = content_type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getCreated_on() {
        return created_on;
    }
    
    public void setCreated_on(Long created_on) {
        this.created_on = created_on;
    }
    
    public Long getGroup_id() {
        return group_id;
    }
    
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }
    
    public Long getReceiver() {
        return receiver;
    }
    
    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }
    
    public String getContent_length() {
        return content_length;
    }
    
    public void setContent_length(String content_length) {
        this.content_length = content_length;
    }

}
