package com.enation.app.b2b2c.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class Navigation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7709232369561209625L;
	
	private Integer id;
	private String name;
	private Integer disable;
	private Integer sort;
	private String contents;
	private String nav_url;
	private Integer target;
	private Integer store_id;
	
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
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getNav_url() {
		return nav_url;
	}
	public void setNav_url(String nav_url) {
		this.nav_url = nav_url;
	}
	
	public Integer getTarget() {
		return target;
	}
	public void setTarget(Integer target) {
		this.target = target;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	
	

}
