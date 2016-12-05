package com.enation.app.b2b2c.core.model.store;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 店铺分类
 * @author LiFenLong
 *
 */
public class StoreCat {
	private Integer store_cat_id;
	private	Integer   store_cat_pid;
	private	Integer   store_id;
	private	String   store_cat_name;
	private	Integer   disable;
	private	Integer   sort;
	private	String   cat_path;
	
	@PrimaryKeyField
	public Integer getStore_cat_id() {
		return store_cat_id;
	}
	public void setStore_cat_id(Integer store_cat_id) {
		this.store_cat_id = store_cat_id;
	}
	public Integer getStore_cat_pid() {
		return store_cat_pid;
	}
	public void setStore_cat_pid(Integer store_cat_pid) {
		this.store_cat_pid = store_cat_pid;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public String getStore_cat_name() {
		return store_cat_name;
	}
	public void setStore_cat_name(String store_cat_name) {
		this.store_cat_name = store_cat_name;
	}
	public Integer getDisable() {
		return disable;
	}
	public void setDisable(Integer disable) {
		this.disable = disable;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getCat_path() {
		return cat_path;
	}
	public void setCat_path(String cat_path) {
		this.cat_path = cat_path;
	}
	
}
