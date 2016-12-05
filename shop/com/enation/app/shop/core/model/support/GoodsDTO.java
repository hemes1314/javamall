package com.enation.app.shop.core.model.support;

import com.enation.app.shop.core.model.Goods;

public class GoodsDTO {
	private Goods goods;
	private String[] photos;
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public String[]getPhotos() {
		return photos;
	}
	public void setPhotos(String[] photos) {
		this.photos = photos;
	}
	
}
