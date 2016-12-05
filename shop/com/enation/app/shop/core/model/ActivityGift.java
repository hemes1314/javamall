package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class ActivityGift implements Serializable {

    private static final long serialVersionUID = 6659797107706003822L;
    
    private int id;
    private int activity_id;
    private int goods_id;
    
    @PrimaryKeyField
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getActivity_id() {
        return activity_id;
    }
    
    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }
    
    public int getGoods_id() {
        return goods_id;
    }
    
    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }
    
}
