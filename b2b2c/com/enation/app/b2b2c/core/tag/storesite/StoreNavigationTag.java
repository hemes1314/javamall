package com.enation.app.b2b2c.core.tag.storesite;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.INavigationManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


@Component
public class StoreNavigationTag extends BaseFreeMarkerTag {

	private INavigationManager navigationManager;
	private IStoreMemberManager storeMemberManager;
	@SuppressWarnings("rawtypes")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer storeid=(Integer) params.get("store_id");
		if(storeid==null){
			StoreMember storeMember = storeMemberManager.getStoreMember();
			storeid = storeMember.getStore_id();
		}
		List list = this.navigationManager.getNavicationList(storeid);
		return list;
	}
	public INavigationManager getNavigationManager() {
		return navigationManager;
	}
	public void setNavigationManager(INavigationManager navigationManager) {
		this.navigationManager = navigationManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
