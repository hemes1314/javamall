package com.enation.app.b2b2c.core.service.order;
/**
 * 店铺订单发货管理类
 * @author fenlongli
 *
 */
public interface IStoreOrderPrintManager {
	/**
	 * 获取订单发货单信息
	 * @param order_id
	 * @return
	 */
	public String getShipScript(Integer order_id);
}
