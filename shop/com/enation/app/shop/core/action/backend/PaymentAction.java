package com.enation.app.shop.core.action.backend;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.RefundLog;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 退款\支付action
 * 
 * @author apexking
 * @author LiFenLong 2014-4-17；4.0改版修改pay方法
 */
public class PaymentAction extends WWAction {

	private PaymentLog payment;
	private RefundLog refund;
	private Integer orderId;
	private IOrderManager orderManager;
	private IStoreOrderManager storeOrderManager;
	private IOrderFlowManager orderFlowManager;
	private IPaymentManager paymentManager;
	private IMemberManager memberManager;
	private IAdminUserManager adminUserManager;
	private StoreOrder ord;
	private List paymentList;

	private List  payLogList;
	
	private IOrderMetaManager orderMetaManager; 
	private IOrderReportManager orderReportManager;
	private Double paymoney; //付款金额
	private int payment_id; //订单Id
	private List<OrderMeta> metaList;
	private Double showMoney;
	private OrderPluginBundle orderPluginBundle;
	/**
	 * 显示确认收款对话框
	 * @param orderId 订单Id,Integer
	 * @param ord 订单,Order
	 * @param payment_id 支付方式Id,Integer
	 * @param paymentList 收款详细列表,List
	 * @param metaList 订单扩展列表,List
	 * @param showMoney 应付金额,Double
	 * @return
	 */
	public String showPayDialog(){
		this.ord = this.storeOrderManager.get(orderId);
		if (this.ord.getParent_id() != null) {
			Order parentOrder = orderManager.get(this.ord.getParent_id());
			this.ord.setParentOrder(parentOrder);
		}
		payment_id = orderReportManager.getPaymentLogId(orderId);
		payment = orderReportManager.getPayment(payment_id);
		paymentList = this.orderReportManager.listPayMentDetail(payment_id);// 查所有收款详细表
		metaList = this.orderMetaManager.list(ord.getOrder_id());
		showMoney=CurrencyUtil.sub(payment.getMoney(), payment.getPaymoney());
 		return "pay_dialog";

	}
	
	/**
	 * 显示退款对话框
	 * @param orderId 订单Id,Integer
	 * @param ord 订单,Order
	 * @return 退款对话框
	 */
	public String showRefundDialog(){
		this.ord = this.storeOrderManager.get(orderId);
		return "refund_dialog";
	}
	
	/**
	 * 显示货到付款付款记录对话框
	 * @param orderId 订单Id,Integer
	 * @param ord 订单,Order
	 * @return 货到付款付款记录对话框
	 */
	public String showPaylogDialog(){
		this.ord = this.storeOrderManager.get(orderId);
		return "pay_log_dialog";
	}
	
	/**
	 * 货到付款付款记录
	 * @param orderId 
	 * @param pay_method 
	 * @param paydate 
	 * @param sn
	 * @param paymoney
	 * @param remark
	 * @param member
	 * @param paymentLog
	 * @return
	 */
	public String pay_log() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String orderId = request.getParameter("orderId");
		String pay_method = request.getParameter("pay_method");
		String paydate = request.getParameter("paydate");
		String sn = request.getParameter("sn");
		String paymoney = request.getParameter("paymoney");
		String remark = request.getParameter("remark");
	 
		
		if(StringUtil.isEmpty(paymoney)){
			this.json="{result:0,message:\"付款金额不能为空，请确认后再提交付款信息！\"}";
			return this.JSON_MESSAGE;
		}
		if(!StringUtil.checkFloat(paymoney, "0+")){
			this.json="{result:0,message:\"付款金额格式不正确，请确认后再提交付款信息！\"}";
			return this.JSON_MESSAGE;
		}
		if(orderId == null || orderId.equals("")){
			this.json="{result:0,message:\"订单号错误，请确认后再提交付款信息！\"}";
			return this.JSON_MESSAGE;
		}
		Order order = orderManager.get(NumberUtils.toInt(orderId));
		if(order == null){
			this.json="{result:0,message:\"订单号错误，请确认后再提交付款信息！\"}";
			return this.JSON_MESSAGE;
		}
		if(!order.getIsCod()){
		if(order.getStatus() == null || order.getStatus().intValue() != OrderStatus.ORDER_NOT_PAY){
			this.json="{result:0,message:\"订单状态错误，请确认后再提交付款信息！\"}";
			return this.JSON_MESSAGE;
		}
		}
		PaymentLog paymentLog =  new PaymentLog();
		
		Member member=null;
		if(order.getMember_id()!=null)
			member=memberManager.get(order.getMember_id());
		
		if(member!=null){			
			paymentLog.setMember_id(member.getMember_id());
			paymentLog.setPay_user(member.getUname());
		}else{
			paymentLog.setPay_user("匿名购买者");
		}
		paymentLog.setPay_date( DateUtil.getDateline(paydate  ));
		paymentLog.setRemark(remark);
		paymentLog.setMoney( Double.valueOf(paymoney) );		
		paymentLog.setOrder_sn(order.getSn());
		paymentLog.setPay_method(pay_method);
		paymentLog.setSn(sn);
		paymentLog.setOrder_id(order.getOrder_id());
		this.showSuccessJson("添加收款成功");
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 支付
	 * @param orderId 订单Id,Integer
	 * @param payment_id 付款单Id,Integer
	 * @param paymoney 付款金额,Double
	 * @return
	 */
	public String pay() {
		try{
			//获取操作用户
			Order order = this.orderManager.get(orderId);
			String username=UserConext.getCurrentAdminUser().getUsername();
			// 调用执行添加收款详细表
			if (orderFlowManager.pay(payment_id, orderId, paymoney,username)) {
				showSuccessJson("订单[" + order.getSn() + "]收款成功");
			} else {
				showErrorJson("订单[" + order.getSn() + "]收款失败,您输入的付款金额合计大于应付金额");
			}
			this.logger.info("确认结算：PaymentAction");
//			orderPluginBundle.confirm(this.orderId,orderManager.get 
//					(orderId).getGoods_amount());
		}catch(RuntimeException e){
			if(logger.isDebugEnabled()){
				logger.debug(e);
			}
			showErrorJson("确认付款失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 退款
	 * @param orderId 订单Id,Integer
	 * @return
	 */
	public String cancel_pay() {
		try{
			refund.setOrder_id(orderId);
			orderFlowManager.refund(refund);
			Order order = this.orderManager.get(orderId);
			this.json="{result:1,message:'订单["+order.getSn()+"]退款成功',payStatus:"+order.getPay_status()+"}";
		}catch(RuntimeException e){
			if(logger.isDebugEnabled()){
				logger.debug(e);
			}
			this.json="{result:0,message:\"退款失败："+e.getMessage()+"\"}";
		}
		return this.JSON_MESSAGE;	 
	}

	public PaymentLog getPayment() {
		return payment;
	}

	public void setPayment(PaymentLog payment) {
		this.payment = payment;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public StoreOrder getOrd() {
		return ord;
	}

	public void setOrd(StoreOrder ord) {
		this.ord = ord;
	}

	public List getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List paymentList) {
		this.paymentList = paymentList;
	}

 
	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}

	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public List getPayLogList() {
		return payLogList;
	}

	public void setPayLogList(List payLogList) {
		this.payLogList = payLogList;
	}

	public RefundLog getRefund() {
		return refund;
	}

	public void setRefund(RefundLog refund) {
		this.refund = refund;
	}

	public Double getPaymoney() {
		return paymoney;
	}

	public void setPaymoney(Double paymoney) {
		this.paymoney = paymoney;
	}

	public int getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(int payment_id) {
		this.payment_id = payment_id;
	}

	public List<OrderMeta> getMetaList() {
		return metaList;
	}

	public void setMetaList(List<OrderMeta> metaList) {
		this.metaList = metaList;
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}

	public Double getShowMoney() {
		return showMoney;
	}

	public void setShowMoney(Double showMoney) {
		this.showMoney = showMoney;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}
	
}
