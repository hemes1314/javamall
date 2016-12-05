package com.enation.app.shop.core.plugin.cart;

import com.enation.app.shop.core.model.Cart;

/**
 * 购物车添加事件
 * @author kingapex
 *
 */
public interface ICartAddEvent {
	public void add(Cart cart);
	
	public void afterAdd(Cart cart);
}
