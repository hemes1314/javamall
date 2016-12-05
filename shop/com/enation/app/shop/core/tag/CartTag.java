package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.impl.ActivityGiftManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 购物车标签
 * @author kingapex
 *2013-8-1上午11:00:20
 */
@Component
@Scope("prototype") 
public class CartTag implements TemplateMethodModel {
	private ICartManager cartManager;
	private ActivityGoodsManager activityGoodsManager;
	private ActivityGiftManager activityGiftManager;
	
	/**
	 * 返回购物车中的购物列表
	 * @param 无 
	 * @return 购物列表 类型List<CartItem>
	 * {@link CartItem}
	 */
	@Override
	public Object exec(List args) throws TemplateModelException {
		 
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		cartManager = SpringContextHolder.getBean("cartManager");
		String sessionid = request.getSession().getId();
		Member member = UserConext.getCurrentMember();
		List<CartItem> goodsList = cartManager.listGoods( sessionid, member==null ? null : member.getMember_id()); //商品列表
		
		for (CartItem cartItem: goodsList) {
		    int goodsId = cartItem.getGoods_id();
		    
		    if (activityGoodsManager.checkGoods(goodsId) > 0) {
		        Map activityMap =  activityGoodsManager.getActivityByGoods(goodsId);
		        cartItem.setActivityName(activityMap.get("name").toString());
		        Object giftName = activityGiftManager.getNameByActivityId(NumberUtils.toLong(activityMap.get("id").toString()), 
		                cartItem.getStore_id()).get("gift_name");
		        
		        if (giftName != null) {
		            cartItem.setGiftName(giftName.toString());
		        }
		    }
		}
		
		return goodsList;
	}
    
    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }
    
    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }
    
    public ICartManager getCartManager() {
        return cartManager;
    }
    
    public void setCartManager(ICartManager cartManager) {
        this.cartManager = cartManager;
    }
    
    public ActivityGiftManager getActivityGiftManager() {
        return activityGiftManager;
    }

    public void setActivityGiftManager(ActivityGiftManager activityGiftManager) {
        this.activityGiftManager = activityGiftManager;
    }

}
