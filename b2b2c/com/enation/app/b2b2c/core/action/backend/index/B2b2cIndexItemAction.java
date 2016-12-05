package com.enation.app.b2b2c.core.action.backend.index;

import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.framework.action.WWAction;

/**
 * 后台首页显示项
 * @author Kanon
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Action("b2b2cIndexItem")
@Results({
	@Result(name="order", type="freemarker", location="/b2b2c/admin/index/order.html")
})
public class B2b2cIndexItemAction extends WWAction {
	private IStoreOrderManager storeOrderManager;
	private Map orderss;
	
	/**
	 * 统计订单状态
	 * @param orderss订单状态,Map
	 * @return 订单统计页
	 */
	public String order(){
		this.orderss  =this.storeOrderManager.censusState();
		return "order";
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public Map getOrderss() {
		return orderss;
	}

	public void setOrderss(Map orderss) {
		this.orderss = orderss;
	}

}
