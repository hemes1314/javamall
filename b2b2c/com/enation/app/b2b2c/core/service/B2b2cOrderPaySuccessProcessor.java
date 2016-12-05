package com.enation.app.b2b2c.core.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.model.RefundLog;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
/**
 * 店铺订单支付成功处理器
 * @author LiFenLong
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class B2b2cOrderPaySuccessProcessor implements IPaySuccessProcessor {
	private IOrderFlowManager orderFlowManager;
	private IOrderManager orderManager; 
	private IOrderReportManager orderReportManager;
	private IDaoSupport daoSupport;
	private IStoreOrderManager storeOrderManager;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void paySuccess(String ordersn, String tradeno, String ordertype,BigDecimal totalFee) {
		StoreOrder order=storeOrderManager.get(ordersn);
		order.setTradeno(tradeno); //2016/2/2 humaodong for支付宝退款
		
		if(order.getPay_status().intValue()== OrderStatus.PAY_CONFIRM ){ //如果是已经支付的，不要再支付
			throw new RuntimeException("订单已经支付，不要再支付");
		}
		this.payConfirmOrder(order);
		if(order.getParent_id()==null){
			//获取子订单列表
			List<StoreOrder> cOrderList= storeOrderManager.storeOrderList(order.getOrder_id());
			for (StoreOrder storeOrder : cOrderList) {
			    storeOrder.setTradeno(tradeno); //2016/2/2 humaodong for支付宝退款
				this.payConfirmOrder(storeOrder);
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void refundSuccess(Order order, BigDecimal refundFee) {
		// 如果是已退款，则不再做退款后处理
		if (order.getRefund_status().intValue() == OrderStatus.PAY_CANCEL) {
			return;
		}
		this.refundConfirmOrder(order, refundFee);
	}
	
	/**
	 * 订单确认退款.
	 * 
	 * @param order 订单
	 */
	private void refundConfirmOrder(Order order, BigDecimal totalFee) {
		RefundLog refundLog = new RefundLog();
		refundLog.setOrder_id(order.getOrder_id());
		refundLog.setOrder_sn(order.getSn());
		refundLog.setMember_id(order.getMember_id());
		refundLog.setType(1);
		refundLog.setMoney(totalFee.doubleValue());
		refundLog.setPay_method(order.getPayment_name());
		refundLog.setPay_date(order.getPayment_time());
		refundLog.setCreate_time(DateUtil.getDateline());
		refundLog.setOp_user("系统");
		refundLog.setRemark("订单退款");
		this.orderFlowManager.refund(refundLog);
	}
	
	/**
	 * 订单确认收款
	 * @param order 订单
	 */
	private void payConfirmOrder(Order order){
		this.orderFlowManager.payConfirm(order.getOrder_id());
		Double needPayMoney= order.getNeed_pay_money(); //在线支付的金额
		int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());
		
		PaymentDetail paymentdetail=new PaymentDetail();
		paymentdetail.setAdmin_user("系统");
		paymentdetail.setPay_date(DateUtil.getDateline());
		paymentdetail.setPay_money(needPayMoney);
		paymentdetail.setPayment_id(paymentid);
		orderReportManager.addPayMentDetail(paymentdetail);
		//修改订单状态为已付款付款
		this.daoSupport.execute("update es_payment_logs set paymoney=paymoney+? where payment_id=?",needPayMoney,paymentid);
		
		//更新订单的已付金额
		this.daoSupport.execute("update es_order set tradeno=?, paymoney=paymoney+? where order_id=?",order.getTradeno(),needPayMoney,order.getOrder_id());
	}
	
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}
	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}
	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
}
