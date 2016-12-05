package com.enation.app.b2b2cAdvbuy.core.tag.advbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.service.impl.AdvBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 预售活动列表标签
 * @author fenlongli
 *
 */
@Component
public class AdvBuyActListTag extends BaseFreeMarkerTag{
	private AdvBuyActiveManager advBuyActiveManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return advBuyActiveManager.listJoinEnable();
	}
	
	public AdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}
	public void setAdvBuyActiveManager(AdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}
}
