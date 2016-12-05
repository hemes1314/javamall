package com.enation.app.shop.core.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单详细页，订单货运日志    whj 
 * @param orderid int型， 订单ID
 *2014-03-04下午8:58:00
 */
@Component
@Scope("prototype")
public class OrderDeliveryListTag extends BaseFreeMarkerTag{
	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private IOrderReportManager orderReportManager;
	private Integer orderid;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Integer orderid  =(Integer)params.get("orderid");
		if(orderid==null){
			throw new TemplateModelException("必须传递orderid参数");
		}
		List<Delivery> deliveryList  = orderReportManager.getDeliveryList(orderid);
		Map result = new HashMap();
		result.put("deliveryList",deliveryList);			
		result.putAll(OrderStatus.getOrderStatusMap());
		return result;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}
	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}
	public Integer getOrderid() {
		return orderid;
	}
	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}
	
}
