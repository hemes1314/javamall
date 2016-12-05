package com.enation.app.b2b2c.component.plugin.store;


import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.goods.StoreTag;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsTagManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 店铺通过添加店铺商品标签
 * @author LiFenLong
 *
 */
@Component
public class StoreAddGoodsTagsPlugin extends AutoRegisterPlugin implements IAfterStorePassEvent{
	private IStoreGoodsTagManager storeGoodsTagManager;
	@Override
	public void AfterStorePass(Store store) {
		StoreTag storeTag=new StoreTag();
		storeTag.setStore_id(store.getStore_id());
		//热卖排行
		storeTag.setTag_name("热卖排行");
		storeTag.setMark("hot");
		storeGoodsTagManager.add(storeTag);
		//新品推荐
		storeTag.setTag_name("新品推荐");
		storeTag.setMark("new");
		storeGoodsTagManager.add(storeTag);
		//推荐商品
		storeTag.setTag_name("推荐商品");
		storeTag.setMark("recommend");
		storeGoodsTagManager.add(storeTag);
	}
	public IStoreGoodsTagManager getStoreGoodsTagManager() {
		return storeGoodsTagManager;
	}
	public void setStoreGoodsTagManager(IStoreGoodsTagManager storeGoodsTagManager) {
		this.storeGoodsTagManager = storeGoodsTagManager;
	}
}
