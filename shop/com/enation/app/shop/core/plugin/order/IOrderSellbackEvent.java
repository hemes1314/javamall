package com.enation.app.shop.core.plugin.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.SellBackList;

/**
 * 订单退货申请事件
 * @author fenlongli
 * @date 2015-6-5 上午1:16:12
 */
public interface IOrderSellbackEvent {

	/**
	 * 提交退货申请
	 * @param sellBackList
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void onOrderSellback(SellBackList sellBackList);
}
