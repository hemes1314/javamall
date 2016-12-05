package com.enation.app.b2b2c.core.model.store;

import com.enation.framework.database.NotDbField;

/**
 * 店铺幻灯片
 * @author LiFenLong
 *
 */
public class StoreSilde {
	private Integer silde_id;	//幻灯片Id
	private Integer store_id;	//店铺Id
	private String silde_url;	//幻灯片URL
	private String img;			//图片

	private String sildeImg;
	@NotDbField
	public String getSildeImg() {
		return sildeImg;
	}
	public void setSildeImg(String sildeImg) {
		this.sildeImg = sildeImg;
	}
	public Integer getSilde_id() {
		return silde_id;
	}
	public void setSilde_id(Integer silde_id) {
		this.silde_id = silde_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public String getSilde_url() {
		return silde_url;
	}
	public void setSilde_url(String silde_url) {
		this.silde_url = silde_url;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
}
