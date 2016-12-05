package com.enation.app.b2b2cSecbuy.core.tag.secbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.service.impl.SecBuyActiveManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 秒拍活动列表标签
 * @author fenlongli
 *
 */
@Component
public class SecBuyActListTag extends BaseFreeMarkerTag{
	private SecBuyActiveManager secBuyActiveManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return secBuyActiveManager.listJoinEnable();
	}
	
	public SecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}
	public void setSecBuyActiveManager(SecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
}
