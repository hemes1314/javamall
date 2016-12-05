package com.enation.app.b2b2cAdvbuy.core.model;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.framework.database.NotDbField;

/**
 * 店铺预售
 * @author fenlongli
 * @date 2015-7-13 上午10:10:41
 */
public class StoreAdvBuy extends AdvBuy{

	private Integer store_id;       // 商家ID 

	/**
	 * 预售对应的店铺商品
	 * 非数据库字段
	 */
	private StoreGoods storeGoods;
	
	@NotDbField
	public StoreGoods getStoreGoods() {
		return storeGoods;
	}
	
	public void setStoreGoods(StoreGoods goods) {
		this.storeGoods = goods;
	}
	
	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	
	
}
