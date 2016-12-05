package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

@SuppressWarnings("serial")
public class FlashAdv implements java.io.Serializable{

	private Integer id;
	private String remark;
	private String url;
	private String pic;
	private Integer sort;
     
     @PrimaryKeyField 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
     
     
}
