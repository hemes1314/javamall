package com.enation.app.b2b2c.core.pluin.order;

import com.enation.app.shop.core.model.support.OrderPrice;

/**
 * 子订单价格计算事件<br>
 * 在创建子订单的时候会调用此事件，计算好的订单价格会填充给子订单
 * @author kingapex
 * @version 1.0
 * 2015年8月21日下午3:05:43
 */
public interface ICountChildOrderPriceEvent {
	
	/**
	 * 计算子订单价格事件
	 * 
	 * @param orderprice 已经计算好的订单价格，如有新的价格，覆盖相应的价格
	 * @return 订单价格
	 */
	public OrderPrice countChildPrice(OrderPrice orderprice);
	
	
}
