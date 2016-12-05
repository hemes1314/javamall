package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 品牌列表标签
 * @author kingapex
 *2013-8-20下午7:58:00
 */
@Component
@Scope("prototype")
public class BrandListTag extends BaseFreeMarkerTag {
	private IBrandManager brandManager;
	/**
	 * @param 不需要输出参数，
	 * @return 返回所有品牌的列表 ，List<Brand>型
	 * {@link Brand}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<Brand> brandList  =brandManager.list();
		return brandList;
	}
	
	public IBrandManager getBrandManager() {
		return brandManager;
	}
	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

}
