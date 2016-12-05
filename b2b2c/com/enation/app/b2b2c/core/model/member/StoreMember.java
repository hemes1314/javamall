package com.enation.app.b2b2c.core.model.member;

import com.enation.app.base.core.model.Member;
/**
 * 店铺会员
 * @author LiFenLong
 *
 */
public class StoreMember extends Member{

	private Integer is_store; //是否有店铺
	private Integer store_id; //店铺Id
	public Integer getIs_store() {
		return is_store;
	}
	public void setIs_store(Integer is_store) {
		this.is_store = is_store;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
}
