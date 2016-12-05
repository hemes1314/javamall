package com.enation.app.shop.core.action.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;


/**
 * 支付api
 * @author kingapex
 *2013-9-4下午7:21:31
 */
 
@SuppressWarnings("serial")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("payment")
public class PaymentApiAction extends WWAction {
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;
	private IMemberAddressManager memberAddressManager;
	private IRegionsManager regionsManager; 
	private Integer addrid;

	
	
	/**
	 * 跳转到第三方支付页面
	 * @param orderid 订单Id
	 * @return
	 */
	public String execute(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		//订单id参数
		Integer orderId=  StringUtil.toInt( request.getParameter("orderid") ,null);
		if(orderId == null ){
			this.json=("必须传递orderid参数");
			return JSON_MESSAGE;
		}
		
		//支付方式id参数
		Integer paymentId=  StringUtil.toInt( request.getParameter("paymentid") ,null);
		Order order = this.orderManager.get(orderId);
		
		if(order==null){
			this.json=("该订单不存在");
			return JSON_MESSAGE;
		}
		
		//如果没有传递支付方式id，则使用订单中的支付方式
		if(paymentId==null){
			paymentId = order.getPayment_id();
			// 20161024-baoxiufeng-解决wap端下的订单在PC端支付时无默认支付方式的问题，此处提供默认支付方式：支付宝
			if (paymentId == null) {
				paymentId = 6;
				order.setPayment_id(paymentId);
			}
		}
		
		PayCfg payCfg = this.paymentManager.get(paymentId);
		IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
		String payhtml = paymentPlugin.onPay(payCfg, order);

		// 用户更换了支付方式，更新订单的数据
		if (order.getPayment_id().intValue() != paymentId.intValue()) {
			this.orderManager.updatePayMethod(orderId, paymentId, payCfg.getType(), payCfg.getName());
		}
		this.json=(payhtml);
		return JSON_MESSAGE;
	}
	


	/**
	 * 检查是否支持货到付款
	 * 
	 * @return result result 1.支持.0.不支持
	 */
	public String checkSupportCod() {
		MemberAddress memberAddress =memberAddressManager.getAddress(addrid);
		try {
		    /********* 2015/12/23 humaodong ************/
		    Integer regionId = memberAddress.getRegion_id();
		    if (regionId == 0 || regionId == null) regionId = memberAddress.getCity_id();
		    if (regionId == 0 || regionId == null) regionId = memberAddress.getProvince_id();
		    /*******************************************/
            if (regionsManager.get(regionId).getCod() == 1) {
            	this.showSuccessJson("支持货到付款");
            } else {
            	this.showErrorJson("不支持货到付款");
            }
        } catch(Exception e) {  
            //如果地区 被删除出现的查询异常，返回不支持货到付款
            this.showErrorJson("不支持货到付款");
        }
		return JSON_MESSAGE;
	}
	
	/**
	 * 检测微信支付结果状态.
	 * 
	 * @return 微信支付结果状态
	 */
	public String checkWxpayStatus() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Integer orderid = NumberUtils.toInt(request.getParameter("orderid"), 0);
		if (orderid > 0) {
			Order order = orderManager.get(orderid);
			if (order.getStatus() != null && OrderStatus.ORDER_PAY_CONFIRM == order.getStatus()) {
				this.showSuccessJson("");
				return JSON_MESSAGE;
			}
		}
		this.showErrorJson("");
		return JSON_MESSAGE;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}


	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}


	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}



	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}



	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}



	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}



	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}



	public Integer getAddrid() {
		return addrid;
	}



	public void setAddrid(Integer addrid) {
		this.addrid = addrid;
	}
	
	
	
}
