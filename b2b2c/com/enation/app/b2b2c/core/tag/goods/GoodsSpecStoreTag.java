package com.enation.app.b2b2c.core.tag.goods;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
@Scope("prototype")
public class GoodsSpecStoreTag extends BaseFreeMarkerTag {
	private IStoreGoodsManager storeGoodsManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goods_id = (Integer) params.get("goods_id");
		if(goods_id==null){
			throw new TemplateModelException("请传入参数goods_id");
		}
		List list = this.storeGoodsManager.getGoodsSpecStore(goods_id);
		return list;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}

}
