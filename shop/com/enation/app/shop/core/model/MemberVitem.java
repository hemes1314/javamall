package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class MemberVitem {
    
    private Integer item_id;
    private long member_id;
    private int type_id;
    private String type_name;
    private String type_image;
    private Double price;
    private int num;
    
    @PrimaryKeyField
    public Integer getItem_id() {
        return item_id;
    }
    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }
    public long getMember_id() {
        return member_id;
    }
    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }
    public int getType_id() {
        return type_id;
    }
    public void setType_id(int type_id) {
        this.type_id = type_id;
    }
    public String getType_name() {
        return type_name;
    }
    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
    public String getType_image() {
        return type_image;
    }
    public void setType_image(String type_image) {
        this.type_image = type_image;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    
}
