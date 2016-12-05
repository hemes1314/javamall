package com.enation.app.groupbuy.component.plugin.cart;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.service.IGroupBuyManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * 
 * @ClassName: GroupGoodsCartPlugin 
 * @Description: 团购商品购物车标签 
 * @author TALON 
 * @date 2015-7-31 上午10:43:51 
 *
 */
public class GroupGoodsCartPlugin extends AutoRegisterPlugin implements ICartAddEvent{
	private IGoodsManager goodsManager;
	private IGroupBuyManager groupBuyManager;
	@Override
	public void add(Cart cart) {
		Map goods=goodsManager.get(cart.getGoods_id());
		if(goods==null) return;
		if (goods.get("is_groupbuy") != null && NumberUtils.toInt(goods.get("is_groupbuy").toString()) == 1) {
			GroupBuy groupbuy=groupBuyManager.getBuyGoodsId(cart.getGoods_id());
			if (groupbuy != null) cart.setPrice(groupbuy.getPrice());
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

	public IGroupBuyManager getGroupBuyManager() {
		return groupBuyManager;
	}

	public void setGroupBuyManager(IGroupBuyManager groupBuyManager) {
		this.groupBuyManager = groupBuyManager;
	}

}
