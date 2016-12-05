package com.enation.app.shop.component.giftcard.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 礼品卡类型实体
 * 
 * @author humaodong 2015-10-11
 */
public class GiftcardType {

	private int type_id;
	private String type_name;
	private String type_image;
	private int money;
	private Double price;

	
	@PrimaryKeyField
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
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getType_image() {
        return type_image;
    }
    public void setType_image(String type_image) {
        this.type_image = type_image;
    }
	
	
}
