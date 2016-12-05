package com.enation.app.b2b2c.core.model.member;

import org.springframework.stereotype.Component;
import com.enation.app.shop.core.model.MemberComment;

/**
 * 店铺评论表
 * @author LiFenLong
 *
 */
public class StoreMemberComment extends MemberComment{

	private int store_desccredit;
	private int store_servicecredit;
	private int store_deliverycredit;
	private int store_id;
	public int getStore_desccredit() {
		return store_desccredit;
	}
	public void setStore_desccredit(int store_desccredit) {
		this.store_desccredit = store_desccredit;
	}
	public int getStore_servicecredit() {
		return store_servicecredit;
	}
	public void setStore_servicecredit(int store_servicecredit) {
		this.store_servicecredit = store_servicecredit;
	}
	public int getStore_deliverycredit() {
		return store_deliverycredit;
	}
	public void setStore_deliverycredit(int store_deliverycredit) {
		this.store_deliverycredit = store_deliverycredit;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	
}
