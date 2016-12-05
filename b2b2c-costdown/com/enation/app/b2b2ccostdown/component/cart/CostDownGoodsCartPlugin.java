package com.enation.app.b2b2ccostdown.component.cart;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.flashbuy.core.model.FlashBuy;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.service.impl.GoodsManager;
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
public class CostDownGoodsCartPlugin extends AutoRegisterPlugin implements ICartAddEvent {

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private CostDownManager costDownManager;

    @Override
    public void add(Cart cart) {
        Map<?, ?> goods = goodsManager.get(cart.getGoods_id());
        if (goods.get("is_cost_down") != null && NumberUtils.toInt(goods.get("is_cost_down").toString()) == 1) {
            CostDown cb = costDownManager.getBuyGoodsId(cart.getGoods_id());
            if(cb != null) cart.setPrice(cb.getPrice());
        }
    }

    @Override
    public void afterAdd(Cart cart) {
    }

}
