package com.enation.app.groupbuy.core.tag.act;

import java.util.Map;


import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: GroupBuyActTag 
 * @Description: 获取当前的活动标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:06 
 *
 */
@Component
public class GroupBuyActTag extends BaseFreeMarkerTag{
	private IGroupBuyActiveManager groupBuyActiveManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		GroupBuyActive groupbuyAct;
		//如果团购活动Id为空则获取当前的团购活动
		if(params.get("act_id")==null){
			 groupbuyAct=groupBuyActiveManager.get();
		}else{
			groupbuyAct = groupBuyActiveManager.get(NumberUtils.toInt((params.get("act_id").toString())));
		}
		if(groupbuyAct==null){
			return "";
		}
		return groupbuyAct;
	}
	public IGroupBuyActiveManager getGroupBuyActiveManager() {
		return groupBuyActiveManager;
	}
	public void setGroupBuyActiveManager(
			IGroupBuyActiveManager groupBuyActiveManager) {
		this.groupBuyActiveManager = groupBuyActiveManager;
	}
}
