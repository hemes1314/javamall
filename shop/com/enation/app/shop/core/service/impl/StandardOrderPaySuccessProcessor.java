package com.enation.app.shop.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;

/**
 * 标准订单支付成功处理器
 * @author kingapex
 *2013-9-24上午11:17:19
 */
@SuppressWarnings("rawtypes")
public class StandardOrderPaySuccessProcessor implements IPaySuccessProcessor {
	private IOrderFlowManager orderFlowManager;
	private IOrderManager orderManager; 
	
	private IAdminUserManager adminUserManager;
	private IOrderReportManager orderReportManager;
	private IDaoSupport daoSupport;
	
	@Override
	public void paySuccess(String ordersn, String tradeno, String ordertype,BigDecimal totalFee) {
		Order order  = orderManager.get(ordersn);
		
		if( order.getPay_status().intValue()== OrderStatus.PAY_CONFIRM ){ //如果是已经支付的，不要再支付
			return ;
		}
		
		this.orderFlowManager.payConfirm(order.getOrder_id());
		
		try{
			//添加支付详细对象 @author LiFenLong
			//AdminUser adminUser = UserConext.getCurrentAdminUser();
			Double needPayMoney= order.getNeed_pay_money(); //在线支付的金额
			int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());
			
			PaymentDetail paymentdetail=new PaymentDetail();
			paymentdetail.setAdmin_user("系统");
			paymentdetail.setPay_date(new Date().getTime());
			paymentdetail.setPay_money(needPayMoney);
			paymentdetail.setPayment_id(paymentid);
			orderReportManager.addPayMentDetail(paymentdetail);
			
			//修改订单状态为已付款付款
			this.daoSupport.execute("update es_payment_logs set paymoney=paymoney+? where payment_id=?",needPayMoney,paymentid);
			
			//更新订单的已付金额
			this.daoSupport.execute("update es_order set paymoney=paymoney+? where order_id=?",needPayMoney,order.getOrder_id());
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	@Override
	public void refundSuccess(Order order, BigDecimal refundFee) {
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


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
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
	
	
}
