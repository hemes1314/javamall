package com.enation.app.b2b2c.core.service.cart;

import com.enation.app.b2b2c.core.model.StoreProduct;
import com.enation.app.shop.core.model.Product;
/**
 * 店铺商品管理类
 * @author LiFenLong
 *
 */
public interface IStoreProductManager {
	/**
	 * 读取某个商品的货品，一般用于无规格商品或捆绑商品
	 * @param goodsid
	 * @return StoreProduct
	 */
	public StoreProduct getByGoodsId(Integer goodsid);
	
	/**
	 * 读取货品详细
	 * @param productid
	 * @return
	 */
	public StoreProduct get(Integer productid);
	
}
