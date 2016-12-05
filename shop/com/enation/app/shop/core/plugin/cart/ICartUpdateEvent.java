package com.enation.app.shop.core.plugin.cart;


/**
 * 购物车修改事件
 * @author Sylow
 * @version v1.0,2015-09-28
 * @since v4.0
 */
public interface ICartUpdateEvent {
	
	
	/**
	 *  当更新时调用这个事件
	 * @param sessionid session Id
	 * @param cartid  购物车id
	 */
	public void onUpdate(String sessionId, Integer cartId);
	
}
