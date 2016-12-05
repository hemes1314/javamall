package com.enation.app.b2b2c.core.model;

import com.enation.app.shop.component.bonus.model.BonusType;

/**
 * 店铺促销实体类
 * @author xulipeng
 *
 */
public class StoreBonus extends BonusType {
	private Integer store_id;	//店铺id
	private String img_bonus;	//图片地址
	private Integer limit_num;		//限领
	private Integer is_given;		//是否允许转增、此版本未使用此字段  xlp
	

	public String getImg_bonus() {
		return img_bonus;
	}

	public void setImg_bonus(String img_bonus) {
		this.img_bonus = img_bonus;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public Integer getLimit_num() {
		return limit_num;
	}

	public void setLimit_num(Integer limit_num) {
		this.limit_num = limit_num;
	}

	public Integer getIs_given() {
		return is_given;
	}

	public void setIs_given(Integer is_given) {
		this.is_given = is_given;
	}

	
	
}
