package com.enation.app.b2b2c.core.action.api.order;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enation.app.b2b2c.core.model.order.OrderLogi;
import com.enation.app.b2b2c.core.service.order.IStoreOrderPrintManager;
import com.enation.app.b2b2c.core.service.order.impl.OrderLogiManager;
import com.enation.framework.action.WWAction;

/**
 * 店铺订单发货 API
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeOrderPrint")
public class StoreOrderPrintApiAction extends WWAction{
	private IStoreOrderPrintManager storeOrderPrintManager;
	private Integer order_id;
	private OrderLogiManager orderLogiManager;
	
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
	
     /**
     * 更新物流信息
     * @param 
     * @return 
     * @add by lin
     */
   public String acceptLogi() {
        HttpServletRequest request = ServletActionContext.getRequest(); 
        String json = "";
        JSONObject myJsonObject = JSONObject.parseObject(json);
        
        OrderLogi orderLogi = new OrderLogi();
        orderLogi.setCom(myJsonObject.getString("com"));
        orderLogi.setComcontact(myJsonObject.getString("comcontact"));
        orderLogi.setCondition(myJsonObject.getString("condition"));
        orderLogi.setKey_id(myJsonObject.getString("keyid"));
        orderLogi.setLogi_num(myJsonObject.getString("nu"));
        
        ArrayList detailList = (ArrayList) myJsonObject.get("data");
      
        orderLogiManager.addUpdateOrderLogi(orderLogi);
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
