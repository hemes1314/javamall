package com.enation.app.shop.core.tag.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 商品属性列表标签
 * @author xulipeng
 */

@Component
@Scope("prototype")
public class GoodsAttributeListTag extends BaseFreeMarkerTag {
	private IGoodsTypeManager goodsTypeManager;
	private IGoodsManager goodsManager;
	/**
	 * @param goodsid 商品Id
	 *  type_id 商品分类Id
	 *  attribute.name 分类名称
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goodsid = (Integer) params.get("goodsid");
		Map goodsmap =this.goodsManager.get(goodsid);
		Integer typeid = (Integer) goodsmap.get("type_id");
		
		List<Attribute> list = this.goodsTypeManager.getAttrListByTypeId(typeid);
		List attrList = new ArrayList();
		
		int i=1;
		for(Attribute attribute:list){
			Map attrmap = new HashMap();
			int type = attribute.getType();
			if(type==3 || type==4 || type == 5){
				String[] s = attribute.getOptionAr();
				String p = (String) goodsmap.get("p"+i);
				Integer num=0;
				if(!StringUtil.isEmpty(p)){
					num = NumberUtils.toInt(p);
				}
				attrmap.put("attrName", attribute.getName());
				attrmap.put("attrValue", s[num]);
			}else if(type==6){
				attrmap.put("attrName", attribute.getName());
				String value=goodsmap.get("p"+i).toString().replace("#", ",").substring(1);
				attrmap.put("attrValue",value);
			}else{
				attrmap.put("attrName", attribute.getName());
				attrmap.put("attrValue", goodsmap.get("p"+i));
			}
			attrList.add(attrmap);
			i++;
		}
		return attrList;
	}
	
	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}
	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
