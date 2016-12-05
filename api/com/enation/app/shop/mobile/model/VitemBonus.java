package com.enation.app.shop.mobile.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class VitemBonus implements Serializable {

    private static final long serialVersionUID = -3069942915659670069L;
    
    private Long id;
    private Long sender;
    private Long vitem_id;
    private Long group_id;
    private Long item_count;
    private Long bonus_count;
    
    @PrimaryKeyField
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSender() {
        return sender;
    }
    
    public void setSender(Long sender) {
        this.sender = sender;
    }
    
    public Long getVitem_id() {
        return vitem_id;
    }
    
    public void setVitem_id(Long vitem_id) {
        this.vitem_id = vitem_id;
    }
    
    public Long getGroup_id() {
        return group_id;
    }
    
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }
    
    public Long getItem_count() {
        return item_count;
    }
    
    public void setItem_count(Long item_count) {
        this.item_count = item_count;
    }
    
    public Long getBonus_count() {
        return bonus_count;
    }
    
    public void setBonus_count(Long bonus_count) {
        this.bonus_count = bonus_count;
    }

}
