package com.enation.app.shop.core.tag.sellback;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 查看退货申请标签
 * @author fenlongli
 *
 */
@Component
public class SellBackTag extends BaseFreeMarkerTag{
	private ISellBackManager sellBackManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer id = NumberUtils.toInt(params.get("id").toString());
		
		return sellBackManager.get(id);
	}
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
}
