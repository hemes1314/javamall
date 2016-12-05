package com.enation.app.secbuy.core.tag.act;

import java.util.Map;


import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: SecBuyActTag 
 * @Description: 获取当前的活动标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:06 
 *
 */
@Component
public class SecBuyActTag extends BaseFreeMarkerTag{
	private ISecBuyActiveManager secBuyActiveManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		SecBuyActive secbuyAct;
		//如果秒拍活动Id为空则获取当前的秒拍活动
		if(params.get("act_id")==null){
			 secbuyAct=secBuyActiveManager.get();
		}else{
			secbuyAct = secBuyActiveManager.get(NumberUtils.toInt(params.get("act_id").toString()));
		}
		if(secbuyAct==null){
			return "";
		}
		return secbuyAct;
	}
	public ISecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}
	public void setSecBuyActiveManager(
			ISecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
}
