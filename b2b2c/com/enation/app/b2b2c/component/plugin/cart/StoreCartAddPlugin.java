package com.enation.app.b2b2c.component.plugin.cart;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.plugin.cart.ICartUpdateEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
public class StoreCartAddPlugin extends AutoRegisterPlugin implements ICartAddEvent, ICartUpdateEvent{
	private IDaoSupport daoSupport;
	private IGoodsManager goodsManager;
	private IStoreCartManager storeCartManager;
	@Override
	public void add(Cart cart) {
		
	}

	@Override
	public void afterAdd(Cart cart) {
		try {
			// 购物车添加店铺Id
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String store_id = request.getParameter("store_id");

			// 如果没有通过request传递过来 就自行查询 20150824 _ add _ by _ Sylow
			if (store_id == null || "".equals(store_id)) {
				int goodsId = cart.getGoods_id();
				Map goods = goodsManager.get(goodsId);
				String storeId = goods.get("store_id").toString();
				store_id = storeId;
			}
			daoSupport.execute("update es_cart set store_id=?, selected='1' where cart_id=?",
					store_id, cart.getCart_id());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.cart.ICartUpdateEvent#onUpdate(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void onUpdate(String sessionId, Integer cartId) {
		
		//重新计算价格
		storeCartManager.countPrice("no");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	
}
