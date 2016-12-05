package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreDlyCenter;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyCenterManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺发货地址标签
 * 根据id查单个
 * @author xulipeng
 *
 */
@Component
public class StoreDlyCenterTag extends BaseFreeMarkerTag {
	private IStoreMemberManager storeMemberManager;
	private IStoreDlyCenterManager storeDlyCenterManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember member=storeMemberManager.getStoreMember();
		Integer dly_id = (Integer) params.get("dly_id");
		StoreDlyCenter dlyCenter = this.storeDlyCenterManager.getDlyCenter(member.getStore_id(), dly_id);
		return dlyCenter;
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
