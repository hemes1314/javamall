package com.enation.app.flashbuy.core.tag.act;

import java.util.Map;


import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: FlashBuyActTag 
 * @Description: 获取当前的活动标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:06 
 *
 */
@Component
public class FlashBuyActTag extends BaseFreeMarkerTag{
	private IFlashBuyActiveManager flashBuyActiveManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		FlashBuyActive flashbuyAct;
		//如果限时抢购活动Id为空则获取当前的限时抢购活动
		if(params.get("act_id")==null){
			 flashbuyAct=flashBuyActiveManager.get();
		}else{
			flashbuyAct = flashBuyActiveManager.get(NumberUtils.toInt(params.get("act_id").toString()));
		}
		if(flashbuyAct==null){
			return "";
		}
		return flashbuyAct;
	}
	public IFlashBuyActiveManager getFlashBuyActiveManager() {
		return flashBuyActiveManager;
	}
	public void setFlashBuyActiveManager(
			IFlashBuyActiveManager flashBuyActiveManager) {
		this.flashBuyActiveManager = flashBuyActiveManager;
	}
}
