package com.enation.app.shop.core.model.support;

import java.util.Map;

//商品编辑数据dto
public class GoodsEditDTO {

	private Map goods; // 编辑的商品数据
	private Map<Integer, String> htmlMap;

	public Map getGoods() {
		return goods;
	}

	public void setGoods(Map goods) {
		this.goods = goods;
	}

	public Map<Integer, String> getHtmlMap() {
		return htmlMap;
	}

	public void setHtmlMap(Map<Integer, String> htmlMap) {
		this.htmlMap = htmlMap;
	}

}
