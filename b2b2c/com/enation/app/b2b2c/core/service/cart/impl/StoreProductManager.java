package com.enation.app.b2b2c.core.service.cart.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreProduct;
import com.enation.app.b2b2c.core.service.cart.IStoreProductManager;
import com.enation.app.shop.core.model.Product;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class StoreProductManager  extends BaseSupport implements IStoreProductManager {

	@Override
	public StoreProduct getByGoodsId(Integer goodsid) {
		String sql ="select * from es_product where goods_id=?";
		List<StoreProduct> proList  =this.daoSupport.queryForList(sql, StoreProduct.class, goodsid);
		if(proList==null || proList.isEmpty()){
			return null;
		}
		return proList.get(0);
	}

	public StoreProduct get(Integer productid) {
		String sql ="select * from es_product where product_id=?";
		return (StoreProduct) this.daoSupport.queryForObject(sql, StoreProduct.class, productid);
	}

}
