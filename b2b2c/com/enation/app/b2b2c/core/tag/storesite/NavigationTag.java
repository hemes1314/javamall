package com.enation.app.b2b2c.core.tag.storesite;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.Navigation;
import com.enation.app.b2b2c.core.service.store.INavigationManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class NavigationTag extends BaseFreeMarkerTag {

	private INavigationManager navigationManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer nav_id = (Integer) params.get("nav_id");
		Navigation navigation =this.navigationManager.getNavication(nav_id);
		return navigation;
	}
	public INavigationManager getNavigationManager() {
		return navigationManager;
	}
	public void setNavigationManager(INavigationManager navigationManager) {
		this.navigationManager = navigationManager;
	}
	
	

}
