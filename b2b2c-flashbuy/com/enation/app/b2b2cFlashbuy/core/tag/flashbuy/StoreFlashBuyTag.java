package com.enation.app.b2b2cFlashbuy.core.tag.flashbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2cFlashbuy.core.model.StoreFlashBuy;
import com.enation.app.b2b2cFlashbuy.core.service.impl.IStoreFlashBuyManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺限时抢购标签
 * @author fenlongli
 * @date 2015-7-14 下午11:42:16
 */
@Component
public class StoreFlashBuyTag extends BaseFreeMarkerTag{
	IStoreFlashBuyManager storeFlashBuyManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		if(params.get("gbid")!=null){
			StoreFlashBuy  flashBuy=storeFlashBuyManager.get((Integer)params.get("gbid"));
			return flashBuy;
		}
		Integer goodsid =(Integer) params.get("goodsid");
		Integer act_id =(Integer) params.get("act_id");
		StoreFlashBuy  flashBuy=storeFlashBuyManager.getBuyGoodsId(goodsid,act_id);
		return flashBuy == null ? "" : flashBuy;
	}
	public IStoreFlashBuyManager getStoreFlashBuyManager() {
		return storeFlashBuyManager;
	}
	public void setStoreFlashBuyManager(IStoreFlashBuyManager storeFlashBuyManager) {
		this.storeFlashBuyManager = storeFlashBuyManager;
	}
	

}
