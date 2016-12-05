package com.enation.app.b2b2c.core.tag.store;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyCenterManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺发货地址标签
 * 根据店铺id查集合
 * @author xulipeng
 */
@Component
public class StoreDlyCenterListTag extends BaseFreeMarkerTag {
	private IStoreMemberManager storeMemberManager;
	private IStoreDlyCenterManager storeDlyCenterManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember member=storeMemberManager.getStoreMember();
		List list = this.storeDlyCenterManager.getDlyCenterList(member.getStore_id());
		return list;
	}
	
	//set get
	
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
