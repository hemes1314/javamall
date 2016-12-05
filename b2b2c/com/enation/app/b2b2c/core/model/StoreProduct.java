package com.enation.app.b2b2c.core.model;

import com.enation.app.shop.core.model.Product;
/**
 * 店铺货品实体
 * @author LiFenLong
 *
 */
public class StoreProduct extends Product{
	private Integer store_id;

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
}
