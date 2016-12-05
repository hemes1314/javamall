package com.enation.app.advbuy.core.model;

import com.enation.app.shop.core.model.Tag;
/**
 * 预售商品标签
 * @author LiFenLong
 *
 */
public class AdvBuyTag extends Tag {

	private Integer is_advbuy;	//是否为预售商品标签

	public Integer getIs_advbuy() {
		return is_advbuy;
	}

	public void setIs_advbuy(Integer is_advbuy) {
		this.is_advbuy = is_advbuy;
	}
	
}
