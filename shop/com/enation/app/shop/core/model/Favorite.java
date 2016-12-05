package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 商品收藏
 * 
 * @author lzf<br/>
 *         2010-3-24 下午02:38:48<br/>
 *         version 1.0<br/>
 */
public class Favorite implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private int favorite_id;
	private long member_id;
	private int goods_id;

	@PrimaryKeyField
	public int getFavorite_id() {
		return favorite_id;
	}

	public void setFavorite_id(int favoriteId) {
		favorite_id = favoriteId;
	}

	public long getMember_id() {
		return member_id;
	}

	public void setMember_id(long memberId) {
		member_id = memberId;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goodsId) {
		goods_id = goodsId;
	}
}
