package com.enation.app.secbuy.core.model;

import com.enation.app.shop.core.model.Tag;
/**
 * 秒拍商品标签
 * @author LiFenLong
 *
 */
public class SecBuyTag extends Tag {

	private Integer is_secbuy;	//是否为秒拍商品标签

	public Integer getIs_secbuy() {
		return is_secbuy;
	}

	public void setIs_secbuy(Integer is_secbuy) {
		this.is_secbuy = is_secbuy;
	}
	
}
