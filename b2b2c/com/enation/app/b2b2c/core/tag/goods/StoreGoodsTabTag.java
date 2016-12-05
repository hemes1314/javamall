package com.enation.app.b2b2c.core.tag.goods;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.pluin.goods.StoreGoodsPluginBundle;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 店铺商品tab显示标签
 * @author fenlongli
 * @date 2015-6-10 上午10:18:02
 */
@Component
public class StoreGoodsTabTag extends BaseFreeMarkerTag{
	private StoreGoodsPluginBundle storeGoodsPluginBundle;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		return storeGoodsPluginBundle.getTabList();
	}
	public StoreGoodsPluginBundle getStoreGoodsPluginBundle() {
		return storeGoodsPluginBundle;
	}
	public void setStoreGoodsPluginBundle(
			StoreGoodsPluginBundle storeGoodsPluginBundle) {
		this.storeGoodsPluginBundle = storeGoodsPluginBundle;
	}
}
