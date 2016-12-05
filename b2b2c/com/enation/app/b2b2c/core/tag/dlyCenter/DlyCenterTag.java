package com.enation.app.b2b2c.core.tag.dlyCenter;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreDlyCenter;
import com.enation.app.b2b2c.core.service.IStoreDlyCenterManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 发货信息标签
 * @author fenlongli
 *
 */
@Component
public class DlyCenterTag extends BaseFreeMarkerTag{
	private IStoreDlyCenterManager storeDlyCenterManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		Integer dlyid = NumberUtils.toInt(param.get("dlyid").toString());
		StoreDlyCenter s= storeDlyCenterManager.getDlyCenter(storeMemberManager.getStoreMember().getStore_id(),dlyid);
		return s;
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
