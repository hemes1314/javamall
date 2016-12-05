package com.enation.app.shop.core.model;

import java.math.BigDecimal;

import com.enation.framework.database.PrimaryKeyField;

public class SommelierOrderType {

	private int id;
	private String order_type_name;
	private String order_type_intro;
	private String order_type_image;
	private BigDecimal order_type_price;

	
	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

    public String getOrder_type_name() {
        return order_type_name;
    }
    
    public void setOrder_type_name(String order_type_name) {
        this.order_type_name = order_type_name;
    }
    
    public BigDecimal getOrder_type_price() {
        return order_type_price;
    }
    
    public void setOrder_type_price(BigDecimal order_type_price) {
        this.order_type_price = order_type_price;
    }
    
    public String getOrder_type_intro() {
        return order_type_intro;
    }
    
    public void setOrder_type_intro(String order_type_intro) {
        this.order_type_intro = order_type_intro;
    }
    
    public String getOrder_type_image() {
        return order_type_image;
    }
    
    public void setOrder_type_image(String order_type_image) {
        this.order_type_image = order_type_image;
    }

}
