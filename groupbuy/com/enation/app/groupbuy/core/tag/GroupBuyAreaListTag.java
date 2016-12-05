package com.enation.app.groupbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.groupbuy.core.service.IGroupBuyAreaManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: GroupBuyAreaListTag 
 * @Description: 团购地区列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:47:07 
 *
 */
@Component
@Scope("prototype")
public class GroupBuyAreaListTag extends BaseFreeMarkerTag {
	private IGroupBuyAreaManager groupBuyAreaManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return groupBuyAreaManager.listAll();
	}
	public IGroupBuyAreaManager getGroupBuyAreaManager() {
		return groupBuyAreaManager;
	}
	public void setGroupBuyAreaManager(IGroupBuyAreaManager groupBuyAreaManager) {
		this.groupBuyAreaManager = groupBuyAreaManager;
	}

	
}

