package com.enation.app.b2b2c.core.tag.goods;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺商品列表
 * @author xulipeng
 *2015年1月7日16:09:40
 */

@Component
public class StoreGoodsListTag extends BaseFreeMarkerTag {

	private IStoreGoodsManager storeGoodsManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Page page = this.storeGoodsManager.storeGoodsList(this.getPage(), 5, params);
		List list = (List) page.getResult();
		return list;
	}

	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}

	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}

	
}
