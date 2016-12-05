package com.enation.app.shop.core.tag.order;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IExpressManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 查询订单快递信息标签
 * @author xulipeng
 *	2015年07月30日16:57:19
 *
 */
@Component
public class OrderExpressInfoTag  extends BaseFreeMarkerTag{

	private IExpressManager expressManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map map = new HashMap();
		try {
			String logino = (String) params.get("logino");
			String code = (String) params.get("code");
			if(logino==null){
				Map result = new HashMap();
				result.put("status", "-1");
				return "";
			}
			if(code == null || code.equals("")){
				code = "yuantong";
			}
			
			map = this.expressManager.getDefPlatform(code.trim(), logino.trim());
		} catch (Exception e) {
			
		}
		
		return map;
	}

	public IExpressManager getExpressManager() {
		return expressManager;
	}

	public void setExpressManager(IExpressManager expressManager) {
		this.expressManager = expressManager;
	}
	
}
