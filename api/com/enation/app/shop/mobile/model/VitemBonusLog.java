package com.enation.app.shop.mobile.model;

import java.io.Serializable;

public class VitemBonusLog implements Serializable {

    private static final long serialVersionUID = -8761571153895148118L;
    
    private Long id;
    private Long vitem_bonus_id;
    private Long member_id;
    private Long item_count;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getVitem_bonus_id() {
        return vitem_bonus_id;
    }
    
    public void setVitem_bonus_id(Long vitem_bonus_id) {
        this.vitem_bonus_id = vitem_bonus_id;
    }
    
    public Long getMember_id() {
        return member_id;
    }
    
    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }
    
    public Long getItem_count() {
        return item_count;
    }
    
    public void setItem_count(Long item_count) {
        this.item_count = item_count;
    }

}
