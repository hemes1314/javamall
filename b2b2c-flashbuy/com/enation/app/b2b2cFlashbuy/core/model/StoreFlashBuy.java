package com.enation.app.b2b2cFlashbuy.core.model;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.framework.database.NotDbField;

/**
 * 店铺限时抢购
 * @author humaodong
 * @date 2015-9-20 上午10:10:41
 */
public class StoreFlashBuy extends FlashBuy{

	private Integer store_id;       // 商家ID 

	/**
	 * 限时抢购对应的店铺商品
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
