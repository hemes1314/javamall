package com.enation.app.b2b2cSecbuy.core.tag.secbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2cSecbuy.core.model.StoreSecBuy;
import com.enation.app.b2b2cSecbuy.core.service.impl.IStoreSecBuyManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺秒拍标签
 * @author fenlongli
 * @date 2015-7-14 下午11:42:16
 */
@Component
public class StoreSecBuyTag extends BaseFreeMarkerTag{
	IStoreSecBuyManager storeSecBuyManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		if(params.get("gbid")!=null){
			StoreSecBuy  secBuy=storeSecBuyManager.get((Integer)params.get("gbid"));
			return secBuy;
		}
		Integer goodsid =(Integer) params.get("goodsid");
		Integer act_id =(Integer) params.get("act_id");
		StoreSecBuy  secBuy=storeSecBuyManager.getBuyGoodsId(goodsid,act_id);
		return secBuy == null ? "" : secBuy;
	}
	public IStoreSecBuyManager getStoreSecBuyManager() {
		return storeSecBuyManager;
	}
	public void setStoreSecBuyManager(IStoreSecBuyManager storeSecBuyManager) {
		this.storeSecBuyManager = storeSecBuyManager;
	}
	

}
