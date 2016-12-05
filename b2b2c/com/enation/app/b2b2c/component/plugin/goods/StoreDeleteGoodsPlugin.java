package com.enation.app.b2b2c.component.plugin.goods;


import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 店铺商品删除插件
 * @author LiFenLong
 *
 */

@Component
public class StoreDeleteGoodsPlugin extends AutoRegisterPlugin implements IGoodsDeleteEvent{
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	private IDaoSupport daoSupport;
	/**
	 * 删除更改店铺商品数量
	 */
	@Override
	public void onGoodsDelete(Integer[] goodsid) {
		for (int i = 0; i < goodsid.length; i++) {
			StoreGoods goods=storeGoodsManager.getGoods(goodsid[i]);
			if(goods.getMarket_enable()==1){
			  //如果是admin删除商品  没有当前在线会员 参数直接从商品对象中获取
//              StoreMember member= storeMemberManager.getStoreMember();
                
                String sql="update es_store set goods_num=goods_num-1 where store_id=?";
                this.daoSupport.execute(sql, goods.getStore_id());
			}
		}
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
