package com.enation.app.shop.core.tag.order;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 通过订单ID，获得该订单下商品的个数
 * @author wanghongjun
 * 2015-04-15 15:05
 */

@Component
@Scope("prototype")
public class OrderDetailGoodsNumTag extends BaseFreeMarkerTag{
	private IOrderManager orderManager;
	private Integer orderid;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer orderid=(Integer) params.get("orderid");
		int count = this.orderManager.getOrderGoodsNum(orderid);
		return count;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public Integer getOrderid() {
		return orderid;
	}

	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}
	
	

}
