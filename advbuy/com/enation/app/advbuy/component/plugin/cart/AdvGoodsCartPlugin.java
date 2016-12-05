package com.enation.app.advbuy.component.plugin.cart;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * 
 * @ClassName: AdvGoodsCartPlugin 
 * @Description: 预售商品购物车标签 
 * @author TALON 
 * @date 2015-7-31 上午10:43:51 
 *
 */
public class AdvGoodsCartPlugin extends AutoRegisterPlugin implements ICartAddEvent{
	private IGoodsManager goodsManager;
	private IAdvBuyManager advBuyManager;
	@Override
	public void add(Cart cart) {
		Map goods=goodsManager.get(cart.getGoods_id());
		if (goods.get("is_advbuy") != null && NumberUtils.toInt(goods.get("is_advbuy").toString()) == 1) {
			AdvBuy advbuy=advBuyManager.getBuyGoodsId(cart.getGoods_id());
			if (advbuy != null) cart.setPrice(advbuy.getPrice());
		}
	}

	@Override
	public void afterAdd(Cart cart) {
		// TODO Auto-generated method stub
		
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IAdvBuyManager getAdvBuyManager() {
		return advBuyManager;
	}

	public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
		this.advBuyManager = advBuyManager;
	}

}
