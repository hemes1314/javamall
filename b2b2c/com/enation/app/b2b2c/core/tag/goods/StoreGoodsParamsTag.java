package com.enation.app.b2b2c.core.tag.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.GoodsType;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 商品属性标签
 * @author fenlongli
 *
 */
@Component
public class StoreGoodsParamsTag extends BaseFreeMarkerTag {
	private IGoodsCatManager goodsCatManager;
	private IGoodsTypeManager goodsTypeManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result=new HashMap();
		Integer catid = NumberUtils.toInt(params.get("catid").toString());
		Cat cat =goodsCatManager.getById(catid);
		int typeid = cat.getType_id();
		GoodsType goodsType = goodsTypeManager.get(typeid);
		
		List attrList = this.goodsTypeManager.getAttrListByTypeId(typeid);
		
		if(goodsType.getJoin_brand()==1){
			List brandList = this.goodsTypeManager.listByTypeId(typeid);
			result.put("brandList", brandList);
		}
		result.put("attrList", attrList);
		return result;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}
	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}
}
