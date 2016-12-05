package com.enation.app.secbuy.component.plugin.cart;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.secbuy.core.model.SecBuy;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * 
 * @ClassName: SecGoodsCartPlugin 
 * @Description: 秒拍商品购物车标签 
 * @author TALON 
 * @date 2015-7-31 上午10:43:51 
 *
 */
public class SecGoodsCartPlugin extends AutoRegisterPlugin implements ICartAddEvent{
	private IGoodsManager goodsManager;
	private ISecBuyManager secBuyManager;
	@Override
	public void add(Cart cart) {
		Map goods=goodsManager.get(cart.getGoods_id());
		if (goods.get("is_secbuy") != null && NumberUtils.toInt(goods.get("is_secbuy").toString()) == 1) {
			SecBuy secbuy=secBuyManager.getBuyGoodsId(cart.getGoods_id());
			if (secbuy != null) cart.setPrice(secbuy.getPrice());
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

	public ISecBuyManager getSecBuyManager() {
		return secBuyManager;
	}

	public void setSecBuyManager(ISecBuyManager secBuyManager) {
		this.secBuyManager = secBuyManager;
	}

}
