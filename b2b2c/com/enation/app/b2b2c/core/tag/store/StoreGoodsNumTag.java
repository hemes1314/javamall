package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 通过店铺ID，获得该店铺 下的商品个数
 * @author wanghongjun
 * 2015-04-20
 */

@Component
@Scope("prototype")
public class StoreGoodsNumTag extends BaseFreeMarkerTag{

	private IStoreOrderManager storeOrderManager;
	private Integer storeid;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer storeid=(Integer) params.get("storeid");
		int count = this.storeOrderManager.getStoreGoodsNum(storeid);
		return count;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public Integer getStoreid() {
		return storeid;
	}

	public void setStoreid(Integer storeid) {
		this.storeid = storeid;
	}
	
	
}
