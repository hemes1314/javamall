package com.enation.app.b2b2cGroupbuy.core.tag.groupbuy;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2cGroupbuy.core.model.StoreGroupBuy;
import com.enation.app.b2b2cGroupbuy.core.service.impl.IStoreGroupBuyManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺团购标签
 * @author fenlongli
 * @date 2015-7-14 下午11:42:16
 */
@Component
public class StoreGroupBuyTag extends BaseFreeMarkerTag{
	IStoreGroupBuyManager storeGroupBuyManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		if(params.get("gbid")!=null){
			StoreGroupBuy  groupBuy=storeGroupBuyManager.get((Integer)params.get("gbid"));
			return groupBuy;
		}
		Integer goodsid =(Integer) params.get("goodsid");
		Integer act_id =(Integer) params.get("act_id");
		StoreGroupBuy  groupBuy=storeGroupBuyManager.getBuyGoodsId(goodsid,act_id);
		return groupBuy == null ? "" : groupBuy;
	}
	public IStoreGroupBuyManager getStoreGroupBuyManager() {
		return storeGroupBuyManager;
	}
	public void setStoreGroupBuyManager(IStoreGroupBuyManager storeGroupBuyManager) {
		this.storeGroupBuyManager = storeGroupBuyManager;
	}
	

}
