package com.enation.app.shop.component.payment.plugin.cod;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;

/**
 * 货到付款支付插件
 * @author kingapex
 *2012-3-30上午10:27:18
 */
@Component("cod")
public class CodPlugin extends AbstractPaymentPlugin implements IPaymentEvent {


	@Override
	public String onCallBack(String ordertype) {
		return "";
	}

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		return "";
	}

	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
		return "";
	}

	@Override
	public String getId() {
		return "cod";
	}

	@Override
	public String getName() {
		return "货到付款";
	}


	@Override
	public String onReturn(String ordertype) {
		return "";
	}

}
