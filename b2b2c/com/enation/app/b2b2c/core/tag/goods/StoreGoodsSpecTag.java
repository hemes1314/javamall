package com.enation.app.b2b2c.core.tag.goods;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺商品规格Tag
 * @author LiFenLong
 *
 */
@Component
public class StoreGoodsSpecTag extends BaseFreeMarkerTag{
	private ISpecManager specManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<Specification> specList = this.specManager.listSpecAndValue();
		return specList;
	}
	public ISpecManager getSpecManager() {
		return specManager;
	}
	public void setSpecManager(ISpecManager specManager) {
		this.specManager = specManager;
	}
}
