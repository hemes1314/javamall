package com.enation.app.shop.mobile.model;

import java.io.Serializable;

public class CountCart implements Serializable {

	private static final long serialVersionUID = 6046862015377548374L;

	private Integer cart_id;
	private Integer cart_num;
	public Integer getCart_id() {
		return cart_id;
	}
	public void setCart_id(Integer cart_id) {
		this.cart_id = cart_id;
	}
	public Integer getCart_num() {
		return cart_num;
	}
	public void setCart_num(Integer cart_num) {
		this.cart_num = cart_num;
	}
	
	
}
