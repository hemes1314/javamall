package com.enation.app.b2b2cAdvbuy.core.tag.advbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2cAdvbuy.core.model.StoreAdvBuy;
import com.enation.app.b2b2cAdvbuy.core.service.impl.IStoreAdvBuyManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺预售标签
 * @author fenlongli
 * @date 2015-7-14 下午11:42:16
 */
@Component
public class StoreAdvBuyTag extends BaseFreeMarkerTag{
	IStoreAdvBuyManager storeAdvBuyManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		if(params.get("gbid")!=null){
			StoreAdvBuy  advBuy=storeAdvBuyManager.get((Integer)params.get("gbid"));
			return advBuy;
		}
		Integer goodsid =(Integer) params.get("goodsid");
		Integer act_id =(Integer) params.get("act_id");
		StoreAdvBuy  advBuy=storeAdvBuyManager.getBuyGoodsId(goodsid,act_id);
		return advBuy == null ? "" : advBuy;
	}
	public IStoreAdvBuyManager getStoreAdvBuyManager() {
		return storeAdvBuyManager;
	}
	public void setStoreAdvBuyManager(IStoreAdvBuyManager storeAdvBuyManager) {
		this.storeAdvBuyManager = storeAdvBuyManager;
	}
	

}
