package com.enation.app.b2b2c.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class StoreTemlplate implements Serializable {

	/**
	 * 店铺物流模板实体类
	 */
	private static final long serialVersionUID = -6119297496435190538L;

	private Integer id;
	private String name;
	private Integer store_id;
	private Integer def_temp;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Integer getDef_temp() {
		return def_temp;
	}
	public void setDef_temp(Integer def_temp) {
		this.def_temp = def_temp;
	}
	
	
}
