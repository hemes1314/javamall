package com.enation.app.b2b2c.core.tag.cart;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.cart.impl.StoreCartManager;
import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.model.StoreCostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.app.b2b2ccostdown.core.service.StoreCostDownManager;
import com.enation.app.shop.core.model.Activity;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.impl.ActivityGiftManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

import javax.servlet.http.HttpServletRequest;

/**
 * 购物车 商品 按商店 分
 * 
 * @author LiFenLong
 *
 */
@Component
public class StoreCartGoodsTag extends BaseFreeMarkerTag {

    @Autowired
    private StoreCartManager<?> storeCartManager;

    private ActivityGoodsManager activityGoodsManager;

    private IGoodsManager goodsManager;
    
    @Autowired
    private StoreCostDownManager storeCostDownManager;
    
    @Autowired
    private CostDownActiveManager costDownActiveManager;

    private ActivityGiftManager activityGiftManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    /**
     * 返回购物车中的购物列表
     * @param 无 
     * @return 购物列表 类型List<CartItem>
     * {@link storeGoodsList}
     */
    protected Object exec(Map params) throws TemplateModelException {

        //重新计算购物车商品价格
        storeCartManager.resetCartPrice();

        //是否计算运费
        String isCountShip = (String) params.get("countship");

        //是否只显示购物车中选中的商品
        String onlySelected = (String) params.get("onlyselected");

        //计算 价格后放入 session
        this.storeCartManager.countPrice(isCountShip, onlySelected != null && onlySelected.equals("yes"));
        List<Map> list = StoreCartContainer.getStoreCartListFromSession();
        
        CostDownActive cda = costDownActiveManager.get();
        for(Map map : list) {
            List<StoreCartItem> cartItems = (List<StoreCartItem>) map.get("goodslist");
            
            for (StoreCartItem storeCartItem : cartItems) {
                int goodsId = storeCartItem.getGoods_id();

                //add by lxl  start
                Goods goods = goodsManager.getGoods(goodsId);
                String disable = goods.getDisabled() == 0 ? "0" : "商品已删除";
                String market_enable = goods.getMarket_enable() == 0 ? "商品已下架" : "1";
                String storeNum = goods.getEnable_store() <= 0 ? "商品库存不足" : "1";
                storeCartItem.setDisable(disable);
                storeCartItem.setMarket_enable(market_enable);
                storeCartItem.setStoreNum(storeNum);
                // end 
                if(activityGoodsManager.checkGoods(goodsId) > 0) {
                    Activity activity = activityGoodsManager.getActivityByGoodsId(goodsId);
                    if(activity!=null){
                        storeCartItem.setActivityName(activity.getName());
                        Object giftName = activityGiftManager.getNameByActivityId(activity.getId().longValue(),
                                storeCartItem.getStore_id()).get("gift_name");
                        
						if(giftName != null) {
                            storeCartItem.setGiftName(giftName.toString());
                        }
                    }
                }
                
                if(cda != null)
                {
                    //判断商品是否参加直降活动
                    StoreCostDown cd = storeCostDownManager.getBuyGoodsId(goodsId, cda.getAct_id());
                    if(cd != null)
                    {
                        storeCartItem.setCostDownId(cd.getAct_id());
                        //活动名称
                        storeCartItem.setCurrent_activity_name(cda.getAct_name());
                        //直降商品名称
                        storeCartItem.setName(cd.getGb_name());
                        //直降图片
                        storeCartItem.setImage_default(cd.getImg_url());
                    }
                }

                //如果商品已删除，或下架，或库存不足更新购物车表中改商品为未选中
                if (goods.getDisabled() != 0 || goods.getMarket_enable() == 0 || goods.getEnable_store() <= 0) {
                    storeCartManager.updateGoodsNoSelected(goodsId);
                }

                //	            storeCartItem.setIsGroupbuy(goodsManager.getGoods(goodsId).getIsGroupbuy().toString());
                //	            storeCartItem.setIsAdvbuy(goodsManager.getGoods(goodsId).getIsAdvbuy().toString());
                //	            storeCartItem.setIsFlashbuy(goodsManager.getGoods(goodsId).getIsFlashbuy().toString());
                //	            storeCartItem.setIsSecbuy(goodsManager.getGoods(goodsId).getIsSecbuy().toString());
            }
            Collections.sort(cartItems);
        }
        return list;
    }

    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }

    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    public ActivityGiftManager getActivityGiftManager() {
        return activityGiftManager;
    }

    public void setActivityGiftManager(ActivityGiftManager activityGiftManager) {
        this.activityGiftManager = activityGiftManager;
    }

}
