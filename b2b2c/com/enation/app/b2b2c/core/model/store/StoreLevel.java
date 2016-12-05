package com.enation.app.b2b2c.core.model.store;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 店铺等级
 * @author LiFenLong
 *
 */
public class StoreLevel implements Serializable{
	private Integer level_id;
	private String level_name;
	@PrimaryKeyField
	public Integer getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

}
