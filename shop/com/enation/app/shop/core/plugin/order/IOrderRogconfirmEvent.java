package com.enation.app.shop.core.plugin.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Order;

public interface IOrderRogconfirmEvent {
	@Transactional(propagation = Propagation.REQUIRED)
	public void rogConfirm(Order order);
}
