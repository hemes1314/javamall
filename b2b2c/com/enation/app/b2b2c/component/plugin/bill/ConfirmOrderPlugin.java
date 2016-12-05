package com.enation.app.b2b2c.component.plugin.bill;
 
import org.springframework.stereotype.Component; 
 
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.shop.core.plugin.order.IConfirmReceiptEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 订单完成创建结算单插件
 * @author Chopper
 * 2015年10月28日15:09:41
 */
@Component
public class ConfirmOrderPlugin extends AutoRegisterPlugin implements IConfirmReceiptEvent{

	private IBillManager billManager;
	@Override
	public void confirm(Integer orderid,double price) { 
			billManager.add_bill(orderid,price);
	}
	public IBillManager getBillManager() {
		return billManager;
	}
	public void setBillManager(IBillManager billManager) {
		this.billManager = billManager;
	}

	
}
