package com.enation.app.flashbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.service.IFlashBuyCatManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: FlashBuyCatListTag 
 * @Description: 限时抢购分类列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:20 
 *
 */
@Component
@Scope("prototype")
public class FlashBuyCatListTag extends BaseFreeMarkerTag {
	private IFlashBuyCatManager flashBuyCatManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return flashBuyCatManager.listAll();
	}
	public IFlashBuyCatManager getFlashBuyCatManager() {
		return flashBuyCatManager;
	}
	public void setFlashBuyCatManager(IFlashBuyCatManager flashBuyCatManager) {
		this.flashBuyCatManager = flashBuyCatManager;
	}

	
}

