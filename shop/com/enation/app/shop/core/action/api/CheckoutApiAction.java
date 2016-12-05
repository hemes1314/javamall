package com.enation.app.shop.core.action.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
/**
 * 结算api
 * @author kingapex
 *2013-7-25上午10:38:13
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("checkout")
public class CheckoutApiAction extends WWAction {
	
	private IMemberAddressManager memberAddressManager;
	private ICartManager cartManager;
	private IDlyTypeManager dlyTypeManager;
	private IPaymentManager paymentManager;
	private String regionId;
	private Integer typeId;
	private String isProtected;
	
	/**
	 * 获取配送方式
	 * @param regionid 收货地区id
	 * @param orderPrice 订单价格
	 * @param weight 订单重量
	 * @return
	 */
	public String getShipingType(){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid  = 	request.getSession().getId();
		Double orderPrice = cartManager.countGoodsTotal(sessionid);
		Double weight = cartManager.countGoodsWeight(sessionid);
		
		List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice, request.getParameter("regionid"));
		this.json  = JsonMessageUtil.getListJson(dlyTypeList);
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 获取支付方式列表
	 * @param paymentList 支付方式列表
	 * @return
	 */
	private String showPaymentList(){
		//读取支付方式列表
		List paymentList  = this.paymentManager.list();
		this.json  = JsonMessageUtil.getListJson(paymentList);
		
		return this.JSON_MESSAGE;
		 
	}
	/**
	 * 显示订单价格信息
	 * @param typeId 配送方式Id
	 * @param regionId 地区Id
	 * @return
	 */
	public  String showOrderTotal(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		OrderPrice  orderPrice  = this.cartManager.countPrice(cartManager.listGoods(sessionid), typeId, regionId);
		this.json = JsonMessageUtil.getObjectJson(orderPrice);
		return this.JSON_MESSAGE;
	}
	
	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
		
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}


	public ICartManager getCartManager() {
		return cartManager;
	}


	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}


	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}


	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}


	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}


	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getIsProtected() {
		return isProtected;
	}

	public void setIsProtected(String isProtected) {
		this.isProtected = isProtected;
	}
	

}
