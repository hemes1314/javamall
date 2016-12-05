package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 错误报告
 * @author kingapex
 * 2010-7-27下午06:13:49
 */
public class ErrorReport {
	private Integer id;
	private String error;
	private String info;
	private long dateline;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public long getDatelineLong() {
		return dateline;
	}
	public void setDateline(long dateline) {
		this.dateline = dateline;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	
	 
}
