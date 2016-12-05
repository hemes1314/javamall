package com.enation.app.shop.core.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 订单详细页，订单日志显示    whj 
 * @param orderid int型， 订单ID
 *2014-03-04下午3:54:32
 */
@Component
@Scope("prototype")
public class OrderDetailLogTag extends BaseFreeMarkerTag{

	private IOrderManager orderManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int orderId  =  StringUtil.toInt(request.getParameter("orderid"),true);
		
		List logList = this.orderManager.listLogs(orderId);
		return  logList;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
