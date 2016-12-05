package com.enation.app.shop.core.plugin.payment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 支付插件基类<br/>
 * @author kingapex
 * 2010-9-25下午02:55:10
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractPaymentPlugin extends AutoRegisterPlugin {
	protected IPaymentManager paymentManager;
	protected IMemberManager memberManager;
	protected IOrderManager orderManager;
	protected ISellBackManager sellBackManager;
	protected IDaoSupport daoSupport;
	private OrderPluginBundle orderPluginBundle;
	protected final Logger logger = Logger.getLogger(getClass());
	
	private String callbackUrl;
	private String showUrl;
	
	@Value("#{configProperties['pay.callback.endpoint']}")
    private String payCallbackEndpoint;
	
	/**
	 * 获取支付相关回调通知地址.
	 * 
	 * @param request 客户端请求
	 * @param serviceUrl 服务地址
	 * @param orderType 订单类型
	 * @param payType 支付类型(对于获取商品展示地址则代表订单编号)
	 * @param subfix 服务名后缀
	 * @return 支付相关回调通知地址
	 */
	private String getPaymentUrl(HttpServletRequest request, String serviceUrl,
			String orderType, String payType, String subfix) {
		StringBuilder url = new StringBuilder("http://");
		if (StringUtils.isBlank(payCallbackEndpoint)) {
			url.append(request.getServerName());
			if (request.getLocalPort() != 80) {
				url.append(":").append(request.getLocalPort());
			}
		} else {
			url.append(payCallbackEndpoint);
		}
		url.append(request.getContextPath());
		if (serviceUrl != null) url.append(serviceUrl);
		if (orderType != null) url.append(orderType).append("_");
		if (payType != null) url.append(payType);
		return url.append(subfix).toString();
	}
	
	/**
	 * 获取退款回调通知地址.
	 * 
	 * @param payCfg 支付配置信息
	 * @param order 支付订单信息
	 * @return 支付回调通知地址
	 */
	protected String getRefundCallbackUrl(PayCfg payCfg, PayEnable order) {
		return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/api/shop/",
				order.getOrderType(), payCfg.getType(), "_refund-callback.do");
	}
	
	/**
	 * 获取支付回调通知地址.
	 * 
	 * @param payCfg 支付配置信息
	 * @param order 支付订单信息
	 * @return 支付回调通知地址
	 */
	protected String getCallBackUrl(PayCfg payCfg, PayEnable order) {
		if (callbackUrl != null) return callbackUrl;
		return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/api/shop/",
				order.getOrderType(), payCfg.getType(), "_payment-callback.do");
	}
	
	/**
	 * 获取支付成功/失败跳转地址.
	 * 
	 * @param payCfg 支付配置信息
	 * @param order 支付订单信息
	 * @return 支付成功/失败跳转地址
	 */
	protected String getReturnUrl(PayCfg payCfg, PayEnable order) {
		return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/",
				order.getOrderType(), payCfg.getType(), "_payment-result.html");
	}
	
	/**
	 * 获取WAP端成功/失败跳转地址.
	 * 
	 * @param payCfg 支付配置信息
	 * @param order 支付订单信息
	 * @return 支付成功/失败跳转地址
	 */
	protected String getReturnWapUrl(PayCfg payCfg, PayEnable order) {
		return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/",
				order.getOrderType(), payCfg.getType(), "_payment-wap-result.html");
	}
	
	/**
	 * 获取商品展示地址.
	 * 
	 * @param order 支付订单信息
	 * @return 商品展示地址
	 */
	protected String getShowUrl(PayEnable order){
		if (showUrl != null) return showUrl;
		if ("s".equals(order.getOrderType())) {
			return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/",
					"orderdetail", order.getSn(), ".html");
		} else {
			return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/",
					order.getOrderType(), order.getSn(), ".html");
		}
	}
	
	/**
	 * 获取WAP端商品展示地址.
	 * 
	 * @param order 支付订单信息
	 * @return 商品展示地址
	 */
	protected String getWapShowUrl(PayEnable order){
		if (showUrl != null) return showUrl;
		if ("s".equals(order.getOrderType())) {
			return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/",
					null, null, "orderlist.html");
		} else {
			return getPaymentUrl(ThreadContextHolder.getHttpRequest(), "/",
					order.getOrderType(), order.getSn(), ".html");
		}
	}
	
	/**
	 * 返回价格字符串
	 * @param price
	 * @return
	 */
	protected String formatPrice(Double price) {
		NumberFormat nFormat = NumberFormat.getNumberInstance();
		nFormat.setMaximumFractionDigits(0);
		nFormat.setGroupingUsed(false);
		return nFormat.format(price);
	}
	
	/**
	 * 获取APP端商品展示地址.
	 * <br/>暂时采用PC端地址.
	 * 
	 * @param order 支付订单信息
	 * @return 商品展示地址
	 */
	@Deprecated
	protected String getAppShowUrl(PayEnable order) {
		return getShowUrl(order);
	}
	
	/**
	 * 设置新的支付回调通知地址.
	 * 
	 * @param url 新的支付回调通知地址
	 */
	public void setCallBackUrl(String url){
		this.callbackUrl = url;
	}
	
	/**
	 * 设置新的商品展示地址.
	 * 
	 * @param url 新的商品展示地址
	 */
	public void setShowUrl(String url) {
		this.showUrl = url;
	}
	
	/**
	 * 获取支付插件设置参数.
	 * 
	 * @return key为参数名称，value为参数值
	 */
	protected Map<String, String> getConfigParams() {
		return this.paymentManager.getConfigParams(this.getId());
	}
	 
	/**
	 * 支付成功后调用此方法来改变订单的状态.
	 * 
	 * @param ordersn 订单编号
	 * @param tradeno 第三方交易流水号
	 * @param ordertype 订单类型
	 * @param totalFee 支付实际回调金额
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void paySuccess(String ordersn, String tradeno, String ordertype,
			BigDecimal totalFee) {
		PaySuccessProcessorFactory.getProcessor(ordertype).paySuccess(ordersn, tradeno, ordertype, totalFee);
		
		// 查询订单信息
		Order order = orderManager.get(ordersn);
		if (!ordertype.equals("topup") && !ordertype.equals("giftcard")
				&& !ordertype.equals("cf")) {
		    this.logger.info("确认结算：AbstractPaymentPlugin");
    		//orderPluginBundle.confirm(order.getOrder_id(),order.getNeed_pay_money());
		}
		// 更新支付方式
		PayCfg payCfg = paymentManager.get(this.getId());
		if (payCfg != null) {
			orderManager.updatePaymentInfo(order.getOrder_id(), payCfg);
		} else {
			orderManager.updatePaymentTime(order.getOrder_id());
		}
		//20160328 Ken,确认支付时间: 加在这里的原因是,加在具体的支付回调里面要改的地方太多了,按说这应该是一个事物的
//		orderManager.updatePaymentTime(order.getOrder_id());
	}
	
	/**
	 * 退款成功后调用此方法来改变订单的状态.
	 * 
	 * @param batchNo 退款流水号
	 * @param refundStatus 退款状态
	 * @param refundFee 退款金额
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void refundSuccess(String batchNo, Integer refundStatus, BigDecimal refundFee) {
		this.daoSupport.execute("update es_order set refund_status=? where refund_batchno=?", refundStatus == null ? 0 : refundStatus, batchNo);
		// 下面的处理暂时忽略，以免影响先前的整个退款流程
//		StoreOrder order = (StoreOrder) this.daoSupport.queryForObject("select * es_order where refund_batchno=?", StoreOrder.class, batchNo);
//		if (order != null) {
//			PaySuccessProcessorFactory.getProcessor(order.getOrderType()).refundSuccess(order, refundFee);
//		}
	}

	/**
	 * 为支付插件定义唯一的id
	 * @return
	 */
	public abstract String getId();

	/**
	 * 定义插件名称
	 * @return
	 */
	public abstract String getName();
	
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
	
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }
}
