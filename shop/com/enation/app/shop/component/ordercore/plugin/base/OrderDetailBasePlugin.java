package com.enation.app.shop.component.ordercore.plugin.base;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.order.IOrderTabShowEvent;
import com.enation.app.shop.core.plugin.order.IShowOrderDetailHtmlEvent;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单详细页基本信息显示插件
 * @author kingapex
 *2012-2-16下午7:20:00
 */
@Component
public class OrderDetailBasePlugin extends AutoRegisterPlugin implements
		IOrderTabShowEvent,IShowOrderDetailHtmlEvent {
	
	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private IOrderReportManager orderReportManager;
	private IDepotManager depotManager;
	private IPaymentManager paymentManager;
	private IDlyTypeManager dlyTypeManager;
	private ILogiManager logiManager;
	@Override
	public boolean canBeExecute(Order order) {
		 
		return true;
	}

	@Override
	public String getTabName(Order order) {
	 
		return "基本信息";
	}

	@Override
	public int getOrder() {
		return 0;
	}
	/**
	 * @param order 订单
	 * member 会员
	 * deliveryList 货运信息
	 * depotList 仓库列表
	 * payCfgList 支付方式列表
	 * OrderStatus 订单状态
	 */
	@Override
	public String onShowOrderDetailHtml(Order order) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		if (order.getMember_id() != null) {
			freeMarkerPaser.putData("member", memberManager.get(order.getMember_id()));
		}
		freeMarkerPaser.putData("deliveryList", orderReportManager.getDeliveryList(order.getOrder_id()));
		freeMarkerPaser.putData("itemList", orderManager.listGoodsItems(order.getOrder_id()));		
		freeMarkerPaser.putData(OrderStatus.getOrderStatusMap());
		freeMarkerPaser.putData("depotList", depotManager.list());
		freeMarkerPaser.putData("payCfgList", paymentManager.list());
		freeMarkerPaser.putData("dlyTypeList", dlyTypeManager.list());
		//物流公司列表
		freeMarkerPaser.putData("logiList", logiManager.list());
		freeMarkerPaser.putData("OrderStatus", OrderStatus.getOrderStatusMap());
		freeMarkerPaser.putData("eop_product", EopSetting.PRODUCT);
		freeMarkerPaser.putData("depot", depotManager.get(order.getDepotid()));
		freeMarkerPaser.setPageName("base");
		return freeMarkerPaser.proessPageContent();
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

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public ILogiManager getLogiManager() {
		return logiManager;
	}

	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}
}
