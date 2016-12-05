package com.enation.app.b2b2c.component.plugin.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;

/**
 * b2b2c子订单项保存事件
 * @author FengXingLong
 * 2015-07-21
 */
public interface IChildItemSaveEvent {
	
	/**
	 * item的id已经填充
	 * @param item
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void onChildItemSave(Order order,OrderItem item);
}
