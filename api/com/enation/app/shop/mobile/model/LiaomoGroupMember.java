package com.enation.app.shop.mobile.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class LiaomoGroupMember implements Serializable {
    
    private static final long serialVersionUID = 7814970025602451133L;
    
    private Long id;
    private Long group_id;
    private Long member_id;
    
    @PrimaryKeyField
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    public Long getGroup_id() {
        return group_id;
    }

    
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    
    public Long getMember_id() {
        return member_id;
    }

    
    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }
    
}
