package com.enation.app.shop.core.plugin.order;

import java.util.List;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;

/**
 * 订单创建前事件
 * @author kingapex
 *2012-4-7下午8:57:06
 */
public interface IBeforeOrderCreateEvent {
	public void onBeforeOrderCreate(Order order ,List<CartItem>   itemList,String sessionid);
}
