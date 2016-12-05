package com.enation.app.shop.component.ordercore.plugin.allocation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.order.IOrderTabShowEvent;
import com.enation.app.shop.core.plugin.order.IShowOrderDetailHtmlEvent;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IOrderAllocationManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.StringUtil;

/**
 * 订单详细页配货信息显示插件
 * @author kingapex
 *2012-2-17下午12:07:09
 */

@Component
public class OrderDetailAllocationPlugin extends AutoRegisterPlugin implements
IOrderTabShowEvent ,IShowOrderDetailHtmlEvent,IAjaxExecuteEnable{
	private IOrderAllocationManager orderAllocationManager;
	private IOrderManager orderManager;
	private IDepotManager depotManager;
	
	/**
	 * 只能配货时显示此选项卡，并执行插件	
	 */
	@Override
	public boolean canBeExecute(Order order) {
		if(order.getShip_status()!= OrderStatus.SHIP_ALLOCATION_NO)
			return true;
		else
			return false;
	}
	
	
	@Override
	public String execute() {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		
	
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int orderId  =  StringUtil.toInt(request.getParameter("orderid"),true);
	 
		long all_time=orderManager.get(orderId).getAllocation_time();
		String all_depotname=depotManager.get(orderManager.get(orderId).getDepotid()).getName();
		List allocationList = orderAllocationManager.listAllocation(orderId);
		
		
		freeMarkerPaser.putData("all_time",all_time);
		freeMarkerPaser.putData("all_depotname",all_depotname);
		freeMarkerPaser.putData("allocationList",allocationList);
		
		freeMarkerPaser.setPageName("allocation_list");
		freeMarkerPaser.setClz(this.getClass());
		return  freeMarkerPaser.proessPageContent();
	}

	@Override
	public String onShowOrderDetailHtml(Order order) {
		
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		int orderId =order.getOrder_id();
		freeMarkerPaser.putData("orderid",orderId);
		freeMarkerPaser.setPageName("allocation");
		
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public String getTabName(Order order) {
		
		
			return "配货信息";
	 
	}

	@Override
	public int getOrder() {
		
		return 9;
	}

	public IOrderAllocationManager getOrderAllocationManager() {
		return orderAllocationManager;
	}

	public void setOrderAllocationManager(
			IOrderAllocationManager orderAllocationManager) {
		this.orderAllocationManager = orderAllocationManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}



}
