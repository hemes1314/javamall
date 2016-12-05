package com.enation.app.shop.core.taglib;

import javax.servlet.jsp.JspException;

import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.taglib.EnationTagSupport;

public class OrderStatusTablib extends EnationTagSupport {
	
	private int status;
	private String type;
	
	public int doEndTag() throws JspException {
		
		if("order".equals(type)){
			String text  = OrderStatus.getOrderStatusText(status);
			this.print(text);
		}
		if("pay".equals(type)){
			String text  = OrderStatus.getPayStatusText(status);
			this.print(text);
		}
		if("ship".equals(type)){
			String text  = OrderStatus.getShipStatusText(status);
			this.print(text);
		}		
		return this.EVAL_BODY_INCLUDE;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
