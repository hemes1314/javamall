package com.enation.app.secbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.service.ISecBuyCatManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: SecBuyCatListTag 
 * @Description: 秒拍分类列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:48:20 
 *
 */
@Component
@Scope("prototype")
public class SecBuyCatListTag extends BaseFreeMarkerTag {
	private ISecBuyCatManager secBuyCatManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return secBuyCatManager.listAll();
	}
	public ISecBuyCatManager getSecBuyCatManager() {
		return secBuyCatManager;
	}
	public void setSecBuyCatManager(ISecBuyCatManager secBuyCatManager) {
		this.secBuyCatManager = secBuyCatManager;
	}

	
}

