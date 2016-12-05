package com.enation.app.shop.core.plugin.order;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.Order;


/**
 * 订单退货事件
 * @author kingapex
 *
 */
public interface IOrderReturnsEvent {
	
	
	/**
	 * 订单退货事件
	 * @param delivery
	 * @param itemList
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void returned(Delivery delivery ,List<DeliveryItem> itemList);
	
}
