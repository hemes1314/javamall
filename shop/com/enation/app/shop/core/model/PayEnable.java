package com.enation.app.shop.core.model;


/**
 * 可支付接口
 * @author kingapex
 *2013-9-23下午3:01:53
 */
public interface PayEnable {
	
	/**
	 * @return 需要支付的金额
	 */
	public Double getNeedPayMoney();
	
	
	/**
	 * @return 订单编号
	 */
	public String getSn();
	
	
	/**
	 * @return 订单类型
	 */
	public String getOrderType();
	
	/**
	 * @return 交易流水号
	 */
	public String getTradeno();
	
	/**
	 * @return 退款交易批次号
	 */
	public String getRefund_batchno();
	
	/**
	 * 订单ID
	 */
	public Integer getOrder_id();
	
	/**
	 * 父级订单ID
	 */
	public Integer getParent_id();
}
