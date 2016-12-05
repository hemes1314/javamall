package com.enation.app.flashbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.service.IFlashBuyAreaManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: FlashBuyAreaListTag 
 * @Description: 限时抢购地区列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:47:07 
 *
 */
@Component
@Scope("prototype")
public class FlashBuyAreaListTag extends BaseFreeMarkerTag {
	private IFlashBuyAreaManager flashBuyAreaManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return flashBuyAreaManager.listAll();
	}
	public IFlashBuyAreaManager getFlashBuyAreaManager() {
		return flashBuyAreaManager;
	}
	public void setFlashBuyAreaManager(IFlashBuyAreaManager flashBuyAreaManager) {
		this.flashBuyAreaManager = flashBuyAreaManager;
	}

	
}

