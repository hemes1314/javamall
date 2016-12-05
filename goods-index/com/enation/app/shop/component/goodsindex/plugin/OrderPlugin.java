package com.enation.app.shop.component.goodsindex.plugin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.order.IOrderRogconfirmEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单插件
 * @author Sylow
 * @version v1.0,2015-11-18
 * @since v5.2
 */
@Component
public class OrderPlugin extends AutoRegisterPlugin implements IOrderRogconfirmEvent {

	private IDaoSupport daoSupport;
	private IGoodsIndexManager goodsIndexManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.order.IOrderRogconfirmEvent#rogConfirm(com.enation.app.shop.core.model.Order)
	 */
	@Override
	public void rogConfirm(Order order) {
		// TODO Auto-generated method stub
		
		String getGoodsIdsSql = "SELECT i.goods_id FROM es_order_items i WHERE i.order_id = " + order.getOrder_id();
		List<Map> list = daoSupport.queryForList(getGoodsIdsSql);
		
		//遍历goodsId
		for (Map map : list) {
			String goodsId = map.get("goods_id").toString();
			String getGoodsSql = "SELECT * FROM es_goods g WHERE g.goods_id = " + goodsId;
			Map goods = daoSupport.queryForMap(getGoodsSql);
			goodsIndexManager.updateIndex(goods);
		}
		
		
	}

	public IGoodsIndexManager getGoodsIndexManager() {
		return goodsIndexManager;
	}

	public void setGoodsIndexManager(IGoodsIndexManager goodsIndexManager) {
		this.goodsIndexManager = goodsIndexManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

}
