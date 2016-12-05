package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


@Component
@Scope("prototype")
public class BrandListByCatidTag extends BaseFreeMarkerTag{

	private IBrandManager brandManager;
	private Integer catid;
	
	/**
	 * 根据catid获得该分类下的品牌列表
	 */
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer catid  =(Integer)params.get("catid");
		List<Brand> brandList  =brandManager.listByCatId(catid);
		return brandList;
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}
	
}
