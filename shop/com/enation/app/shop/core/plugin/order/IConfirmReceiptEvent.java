package com.enation.app.shop.core.plugin.order;

import java.util.List;
 

/**
 * 确认收货事件
 * 确认收货后，b2b2c会进行结算价格
 * @author Chopper
 * @version 1.0
 * 2015年10月28日10:46:32
 */
public interface IConfirmReceiptEvent {
	
	/**
	 * 确认结算
	 * @param orderprice
	 * @return
	 */
	public void confirm(Integer orderid,double price);
	
	
}
