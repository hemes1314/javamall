package com.enation.app.shop.core.plugin.payment;

import java.math.BigDecimal;

import com.enation.app.shop.core.model.Order;

/**
 * 支付成功处理器
 * @author kingapex
 *2013-9-24上午10:56:24
 */
public interface IPaySuccessProcessor {
	
	/**
	 * 对于支付成功的处理方法
	 * @param orderSn 订单编号
	 * @param tradeNo 第三方交易流水号
	 * @param orderType 订单类型 已知的：standard 标准订单，credit:信用账户充值
	 * @param totalFee  支付宝实际回调金额
	 */
	public void paySuccess(String orderSn, String tradeNo, String orderType,
			BigDecimal totalFee);
	
	/**
	 * 退款申请成功的处理方法.
	 * 
	 * @param order 订单信息
	 * @param refundFee 退款金额
	 */
	public void refundSuccess(Order order, BigDecimal refundFee);
}
