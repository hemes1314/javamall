package com.enation.app.b2b2c.core.tag.dlyCenter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyCenterManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 发货设置列表标签
 * @author fenlongli
 *
 */
@Component
public class DlyCenterListTag extends BaseFreeMarkerTag{
	private IStoreDlyCenterManager storeDlyCenterManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember member=storeMemberManager.getStoreMember();
		return storeDlyCenterManager.getDlyCenterList(member.getStore_id());
	}
	
	public IStoreDlyCenterManager getStoreDlyCenterManager() {
		return storeDlyCenterManager;
	}
	public void setStoreDlyCenterManager(
			IStoreDlyCenterManager storeDlyCenterManager) {
		this.storeDlyCenterManager = storeDlyCenterManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
