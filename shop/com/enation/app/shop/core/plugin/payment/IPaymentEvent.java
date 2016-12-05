package com.enation.app.shop.core.plugin.payment;

import java.math.BigDecimal;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;

/**
 * 在线支付事件
 * @author kingapex
 *
 */
public interface IPaymentEvent {
	
	/**
	 * 生成跳转至第三方支付平台的html和脚本
	 * @param payCfg
	 * @param order 可支付的对象
	 * @return 跳转到第三方支付平台的html和脚本
	 */
	public String onPay(PayCfg  payCfg,PayEnable order);
	
	
	/**
	 * 支付成功后异步通知事件的
	 * @param ordertype 订单类型，standard 标准订单，credit:信用账户充值
	 * @return 返回第三方支付需要的信息
	 * 
	 */
	public String onCallBack(String ordertype);
	
	
	
	/**
	 * 支付成功后返回本站后激发此事件 
	 * @param ordertype 订单类型，standard 标准订单，credit:信用账户充值
	 * @return  要求返回订单sn
	 */
	public String onReturn(String ordertype);
	
	/**
	 * 退款请求事件.
	 * 
	 * @return
	 */
	public String onRefund(BigDecimal refundPrice, PayCfg  payCfg, PayEnable order);
}	
