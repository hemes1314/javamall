package com.enation.app.b2b2c.core.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.CurrencyUtil;

import freemarker.template.TemplateModelException;
/**
 * 订单支付详细标签
 * @author LiFenLong
 *
 */
@Component
public class PaymentInfoTag extends BaseFreeMarkerTag{
	private IOrderReportManager orderReportManager;
	private IOrderManager orderManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String sn=(String) params.get("ordersn");
		Order order= orderManager.get(sn);
		Integer payment_id = orderReportManager.getPaymentLogId(order.getOrder_id());
		PaymentLog payment = orderReportManager.getPayment(payment_id);
		List<PaymentDetail> paymentList = this.orderReportManager.listPayMentDetail(payment_id);// 查所有收款详细表
		Double showMoney=CurrencyUtil.sub(payment.getMoney(), payment.getPaymoney());
		Map result=new HashMap();
		result.put("paymentList", paymentList);
		result.put("payment", payment);
		result.put("order", order);
		result.put("showMoney", showMoney);
		result.put("payment_id", payment_id);
		return result;
	}
	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}
	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
