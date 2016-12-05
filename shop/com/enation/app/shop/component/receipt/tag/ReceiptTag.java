package com.enation.app.shop.component.receipt.tag;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 发票标签
 * @author whj
 *2013-7-29下午9:45:44
 */
@Component
@Scope("prototype")
public class ReceiptTag extends BaseFreeMarkerTag{
	
	private IReceiptManager receiptManager;
	private IOrderManager orderManager;

	/**
	 * @param orderid 订单id,int型 必须
	 * @return 发票实体 {@link Receipt}
	 * 如果该订单不存在发票，返回null。
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer orderid = (Integer) params.get("orderid");
			 Receipt receipt = receiptManager.getByOrderid(orderid);
			 if(receipt==null){
				receipt = new Receipt();   
			 }
			 return receipt;

	}
	
	public IReceiptManager getReceiptManager() {
		return receiptManager;
	}

	public void setReceiptManager(IReceiptManager receiptManager) {
		this.receiptManager = receiptManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
}
