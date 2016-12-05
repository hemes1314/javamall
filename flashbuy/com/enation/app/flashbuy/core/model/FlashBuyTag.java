package com.enation.app.flashbuy.core.model;

import com.enation.app.shop.core.model.Tag;
/**
 * 限时抢购商品标签
 * @author LiFenLong
 *
 */
public class FlashBuyTag extends Tag {

	private Integer is_flashbuy;	//是否为限时抢购商品标签

	public Integer getIs_flashbuy() {
		return is_flashbuy;
	}

	public void setIs_flashbuy(Integer is_flashbuy) {
		this.is_flashbuy = is_flashbuy;
	}
	
}
