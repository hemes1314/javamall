package com.enation.app.b2b2c.core.tag.goods;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class StoreSearchGoodsListTag extends BaseFreeMarkerTag {

	private IStoreGoodsManager storeGoodsManager;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer num = (Integer) params.get("num");
		if(num==null || num==0){
			num=this.getPageSize();
		}
		Page webpage = this.storeGoodsManager.store_searchGoodsList(this.getPage(), num, params);
		Long totalCount = webpage.getTotalCount();
		Map result = new HashMap();
		//map.put(arg0, arg1);
		result.put("page", this.getPage());
		result.put("pageSize", num);
		result.put("totalCount", totalCount);
		result.put("storegoods", webpage);
		return result;
	}

	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}

	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	
	

}
