package com.enation.app.advbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.service.IAdvBuyCatManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: AdvBuyCatListTag 
 * @Description: 预售分类列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:20 
 *
 */
@Component
@Scope("prototype")
public class AdvBuyCatListTag extends BaseFreeMarkerTag {
	private IAdvBuyCatManager advBuyCatManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return advBuyCatManager.listAll();
	}
	public IAdvBuyCatManager getAdvBuyCatManager() {
		return advBuyCatManager;
	}
	public void setAdvBuyCatManager(IAdvBuyCatManager advBuyCatManager) {
		this.advBuyCatManager = advBuyCatManager;
	}

	
}

