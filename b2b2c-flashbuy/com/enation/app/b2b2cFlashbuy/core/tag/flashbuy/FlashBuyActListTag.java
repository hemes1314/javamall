package com.enation.app.b2b2cFlashbuy.core.tag.flashbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.service.impl.FlashBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 限时抢购活动列表标签
 * @author fenlongli
 *
 */
@Component
public class FlashBuyActListTag extends BaseFreeMarkerTag{
	private FlashBuyActiveManager flashBuyActiveManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return flashBuyActiveManager.listJoinEnable();
	}
	
	public FlashBuyActiveManager getFlashBuyActiveManager() {
		return flashBuyActiveManager;
	}
	public void setFlashBuyActiveManager(FlashBuyActiveManager flashBuyActiveManager) {
		this.flashBuyActiveManager = flashBuyActiveManager;
	}
}
