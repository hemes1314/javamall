package com.enation.app.flashbuy.component.plugin.cart;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * 
 * @ClassName: FlashGoodsCartPlugin 
 * @Description: 限时抢购商品购物车标签 
 * @author humaodong 
 * @date 2015-7-31 上午10:43:51 
 *
 */
public class FlashGoodsCartPlugin extends AutoRegisterPlugin implements ICartAddEvent{
	private IGoodsManager goodsManager;
	private IFlashBuyManager flashBuyManager;
	@Override
	public void add(Cart cart) {
		Map goods=goodsManager.get(cart.getGoods_id());
		if (goods.get("is_flashbuy") != null && NumberUtils.toInt(goods.get("is_flashbuy").toString()) == 1) {
			FlashBuy flashbuy=flashBuyManager.getBuyGoodsId(cart.getGoods_id());
			if (flashbuy != null) cart.setPrice(flashbuy.getPrice());
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

	public IFlashBuyManager getFlashBuyManager() {
		return flashBuyManager;
	}

	public void setFlashBuyManager(IFlashBuyManager flashBuyManager) {
		this.flashBuyManager = flashBuyManager;
	}

}
