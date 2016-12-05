package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;



/**
 * 获得该会员订单在各个状体下的个数
 * @author wanghongjun    2015-04-17
 *
 */
@Component
@Scope("prototype")
public class storeMemberOrderNumTag extends BaseFreeMarkerTag{
	
	private IStoreOrderManager storeOrderManager;
	private Integer status;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer status=(Integer) params.get("status");
		int count = this.storeOrderManager.orderStatusNum(status);
		return count;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	

}
