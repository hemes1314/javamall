package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreLogiCompanyManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺物流公司列表
 * @author fenlongli
 *
 */
@Component
public class StoreLogiCompanyListTag extends BaseFreeMarkerTag {
	private IStoreLogiCompanyManager storeLogiCompanyManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		return storeLogiCompanyManager.listByStore();
	}
	public IStoreLogiCompanyManager getStoreLogiCompanyManager() {
		return storeLogiCompanyManager;
	}
	public void setStoreLogiCompanyManager(
			IStoreLogiCompanyManager storeLogiCompanyManager) {
		this.storeLogiCompanyManager = storeLogiCompanyManager;
	}
}
