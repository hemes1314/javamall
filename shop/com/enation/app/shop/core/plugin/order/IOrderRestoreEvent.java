package com.enation.app.shop.core.plugin.order;

import com.enation.app.shop.core.model.Order;

public interface IOrderRestoreEvent {
	public void restore(Order order);
}
