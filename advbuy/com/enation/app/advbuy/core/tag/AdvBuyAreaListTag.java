package com.enation.app.advbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.service.IAdvBuyAreaManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: AdvBuyAreaListTag 
 * @Description: 预售地区列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:47:07 
 *
 */
@Component
@Scope("prototype")
public class AdvBuyAreaListTag extends BaseFreeMarkerTag {
	private IAdvBuyAreaManager advBuyAreaManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return advBuyAreaManager.listAll();
	}
	public IAdvBuyAreaManager getAdvBuyAreaManager() {
		return advBuyAreaManager;
	}
	public void setAdvBuyAreaManager(IAdvBuyAreaManager advBuyAreaManager) {
		this.advBuyAreaManager = advBuyAreaManager;
	}

	
}

