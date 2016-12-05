package com.enation.app.base.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 权限点实体
 * @author kingapex
 * 2010-10-24下午12:38:51
 */
public class AuthAction implements Serializable{


	private static final long serialVersionUID = 4831369457068242964L;
	
	
	private Integer actid;
	private String name;
	private String type;
	private String objvalue;
	
	private int choose;
	@PrimaryKeyField
	public Integer getActid() {
		return actid;
	}
	public void setActid(Integer actid) {
		this.actid = actid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getObjvalue() {
		return objvalue;
	}
	public void setObjvalue(String objvalue) {
		this.objvalue = objvalue;
	}
	public int getChoose() {
		return choose;
	}
	public void setChoose(int choose) {
		this.choose = choose;
	}
}
