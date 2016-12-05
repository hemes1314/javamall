package com.enation.app.b2b2c.core.action.backend.order;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderPrintManager;
import com.enation.framework.action.WWAction;
/**
 * 店铺发货订单Action
 * @author fenlongli
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
})
@Action("storeOrderPrint")
public class StoreOrderPrintAction extends WWAction{
	private IStoreOrderPrintManager storeOrderPrintManager;
	private Integer order_id;
	
	/**
	 * 打印发货单
	 * @param script 打印的script,String
	 * @return 发货单的script
	 */
	public String shipScript() {
		String script= storeOrderPrintManager.getShipScript(order_id);
		this.json=script;
		return this.JSON_MESSAGE;
	}

	public IStoreOrderPrintManager getStoreOrderPrintManager() {
		return storeOrderPrintManager;
	}

	public void setStoreOrderPrintManager(
			IStoreOrderPrintManager storeOrderPrintManager) {
		this.storeOrderPrintManager = storeOrderPrintManager;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
}
