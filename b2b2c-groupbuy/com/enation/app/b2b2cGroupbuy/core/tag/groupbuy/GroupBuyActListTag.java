package com.enation.app.b2b2cGroupbuy.core.tag.groupbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.groupbuy.core.service.impl.GroupBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 团购活动列表标签
 * @author fenlongli
 *
 */
@Component
public class GroupBuyActListTag extends BaseFreeMarkerTag{
	private GroupBuyActiveManager groupBuyActiveManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return groupBuyActiveManager.listJoinEnable();
	}
	
	public GroupBuyActiveManager getGroupBuyActiveManager() {
		return groupBuyActiveManager;
	}
	public void setGroupBuyActiveManager(GroupBuyActiveManager groupBuyActiveManager) {
		this.groupBuyActiveManager = groupBuyActiveManager;
	}
}
