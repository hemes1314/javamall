package com.enation.app.advbuy.core.tag.act;

import java.util.Map;


import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: AdvBuyActTag 
 * @Description: 获取当前的活动标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:06 
 *
 */
@Component
public class AdvBuyActTag extends BaseFreeMarkerTag{
	private IAdvBuyActiveManager advBuyActiveManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		AdvBuyActive advbuyAct;
		//如果预售活动Id为空则获取当前的预售活动
		if(params.get("act_id")==null){
			 advbuyAct=advBuyActiveManager.get();
		}else{
			advbuyAct = advBuyActiveManager.get(NumberUtils.toInt((params.get("act_id").toString())));
		}
		if(advbuyAct==null){
			return "";
		}
		return advbuyAct;
	}
	public IAdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}
	public void setAdvBuyActiveManager(
			IAdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}
}
