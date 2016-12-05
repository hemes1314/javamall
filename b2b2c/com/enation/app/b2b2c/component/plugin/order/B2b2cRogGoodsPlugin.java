package com.enation.app.b2b2c.component.plugin.order;

import java.util.List;

import com.enation.framework.cache.CacheFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.plugin.order.IOrderRogconfirmEvent;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/***
 * b2b2c收货后添加商品购买数量
 * @author LiFenLong
 *
 */
public class B2b2cRogGoodsPlugin extends AutoRegisterPlugin implements IOrderRogconfirmEvent{
	private IOrderManager orderManager;
	private IDaoSupport daoSupport;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void rogConfirm(Order order) {
		List<OrderItem> orderItemList=orderManager.listGoodsItems(order.getOrder_id());
		
		for (OrderItem orderItem : orderItemList) {
			String sql="update es_goods set buy_num=buy_num+? where goods_id=?";
			this.daoSupport.execute(sql,orderItem.getNum(),orderItem.getGoods_id());
			   //hp清除缓存
            com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(orderItem.getGoods_id()));
    
		}
		
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
