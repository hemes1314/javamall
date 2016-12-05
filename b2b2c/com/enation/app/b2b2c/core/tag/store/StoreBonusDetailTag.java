package com.enation.app.b2b2c.core.tag.store;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 查询某一个优惠券的详细信息
 * @author xulipeng
 *
 */
@Component
public class StoreBonusDetailTag extends BaseFreeMarkerTag {

	private IStoreBonusManager storeBonusManager;
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		Integer bonusid = (Integer) param.get("bonusid");
		StoreBonus bonus = storeBonusManager.get(bonusid);
		return bonus;
	}
	
	public IStoreBonusManager getStoreBonusManager() {
		return storeBonusManager;
	}
	public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
		this.storeBonusManager = storeBonusManager;
	}

}
