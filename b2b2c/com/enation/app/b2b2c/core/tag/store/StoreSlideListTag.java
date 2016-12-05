package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreSildeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 幻灯片列表
 * @author LiFenLong
 *
 */
public class StoreSlideListTag extends BaseFreeMarkerTag{
	private IStoreSildeManager storeSildeManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Integer storeid = (Integer) params.get("storeid");
		if(storeid==null){
			StoreMember member=storeMemberManager.getStoreMember();;
			storeid = member.getStore_id();
		}
		return storeSildeManager.list(storeid);
	}
	public IStoreSildeManager getStoreSildeManager() {
		return storeSildeManager;
	}
	public void setStoreSildeManager(IStoreSildeManager storeSildeManager) {
		this.storeSildeManager = storeSildeManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
