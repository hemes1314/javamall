package com.enation.app.b2b2c.core.tag.store;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreRegionsManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺地区标签
 * @author xulipeng
 * 2015年1月12日14:28:25
 */

@Component
public class StoreRegionsListTag extends BaseFreeMarkerTag {
	
	private IStoreRegionsManager storeRegionsManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List list = this.storeRegionsManager.getRegionsToAreaList();
		return list;
	}

	public IStoreRegionsManager getStoreRegionsManager() {
		return storeRegionsManager;
	}

	public void setStoreRegionsManager(IStoreRegionsManager storeRegionsManager) {
		this.storeRegionsManager = storeRegionsManager;
	}

}
