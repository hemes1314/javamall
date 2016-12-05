package com.enation.app.shop.component.promotion.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.promotion.IPromotionPlugin;
import com.enation.app.shop.core.service.promotion.PromotionConditions;
import com.enation.app.shop.core.service.promotion.PromotionType;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
public class GoodsDiscountPlugin extends AutoRegisterPlugin implements IPromotionPlugin {

	
	public String[] getConditions() {
		return new String[]{ PromotionConditions.GOODS ,PromotionConditions.MEMBERLV};
	}

	
	public String getMethods() {
		return "discount";
	}

	
	public String getAuthor() {
		return "kingapex";
	}

	
	public String getId() {
		return "goodsDiscountPlugin";
	}

	
	public String getName() {
		return "打折————商品直接打折，如全场女鞋8折。可以对商品任意折扣，适合低价清货促销";
	}

	
	public String getType() {
		return PromotionType.PMTTYPE_GOODS;
	}

	
	public String getVersion() {
		return "1.0";
	}

	
	public void perform(Object... params) {

	}

	
	public void register() {
		
	}

}
