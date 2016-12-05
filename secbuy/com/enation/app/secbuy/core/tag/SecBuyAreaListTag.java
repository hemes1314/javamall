package com.enation.app.secbuy.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.service.ISecBuyAreaManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: SecBuyAreaListTag 
 * @Description: 秒拍地区列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:47:07 
 *
 */
@Component
@Scope("prototype")
public class SecBuyAreaListTag extends BaseFreeMarkerTag {
	private ISecBuyAreaManager secBuyAreaManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return secBuyAreaManager.listAll();
	}
	public ISecBuyAreaManager getSecBuyAreaManager() {
		return secBuyAreaManager;
	}
	public void setSecBuyAreaManager(ISecBuyAreaManager secBuyAreaManager) {
		this.secBuyAreaManager = secBuyAreaManager;
	}

	
}

