package com.enation.app.b2b2c.core.action.api.activity;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.service.impl.AdvBuyManager;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.service.impl.GroupBuyManager;
import com.enation.app.shop.core.model.Activity;
import com.enation.app.shop.core.service.impl.ActivityGiftManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.app.shop.core.service.impl.GoodsManager;
import com.enation.app.utils.PriceUtils;
import com.enation.app.utils.PriceUtils.IActivity;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("activity")
public class ActivityApiAction extends WWAction {

    private Integer activity_id;
    private String goods_ids;
    private Integer storeId;
    private Integer activityId;
    private Integer goodsId;
    private ActivityGoodsManager activityGoodsManager;
    private ActivityGiftManager activityGiftManager;
    private GoodsManager goodsManager;
    
    @Autowired
    private GroupBuyManager groupBuyManager;
    
    @Autowired
    private CostDownManager costDownManager;
    
    @Autowired
    private AdvBuyManager advBuyManager;
    
    public String saveActivityGoods() {
        activityGoodsManager.add(activity_id, goods_ids);
        this.showSuccessJson("促销活动商品添加成功");
        return this.JSON_MESSAGE;
    }
    
    public String delActivityGoods() {
        activityGoodsManager.delete(activity_id, goods_ids);
        this.showSuccessJson("促销活动商品删除成功");
        return this.JSON_MESSAGE;
    }
    
    public String saveActivityGift() {
        activityGiftManager.add(activity_id, goods_ids);
        this.showSuccessJson("赠送商品添加成功");
        return this.JSON_MESSAGE;
    }
    
    public String delActivityGift() {
        activityGiftManager.delete(activity_id, goods_ids);
        this.showSuccessJson("促销活动商品删除成功");
        return this.JSON_MESSAGE;
    }
    
    public String getGoodsActivity() {
//        Map map = activityGoodsManager.getActivityByGoods(goodsId);
//        Map<String, Object> returnMap = new HashMap<String, Object>();
//        
//        if (map != null) {
//            String activityName = map.get("name").toString();
//            Map giftMap = activityGiftManager.getNameByActivityId(NumberUtils.toLong(map.get("id").toString()), 
//                    NumberUtils.toInt(map.get("store_id").toString()));
//            returnMap.put("activityName", activityName);
//            returnMap.put("giftName", giftMap.get("gift_name"));
//            returnMap.put("giftId", giftMap.get("gift_id"));
//            this.json = JsonMessageUtil.getMobileObjectJson(returnMap);
//        } else {
//            this.json = JsonMessageUtil.getMobileErrorJson("no activity");
//        }
        Activity activity = activityGoodsManager.getActivityByGoodsId(goodsId);
        
        GroupBuy gb = groupBuyManager.getBuyGoodsId(goodsId);        
        AdvBuy ab = advBuyManager.getBuyGoodsId(goodsId);        
        CostDown cd = costDownManager.getBuyGoodsId(goodsId);
        IActivity a = PriceUtils.getMinimumPriceActivity(gb, ab, cd);
        //如果是没有活动 或者 活动为直降 的话  参与 满减
        if (null != activity && (null == a || a.equals(cd))) {
            this.json = JsonMessageUtil.getMobileObjectJson(activity);
        } else {
            this.json = JsonMessageUtil.getMobileErrorJson("no activity");
        }
        return this.JSON_MESSAGE;
    }
    
//    public Page getActivityGoods() {
//        return goodsManager.searchGoodsForActivity(storeId, activityId, page, pageSize);
//    }
    
    public Integer getActivity_id() {
        return activity_id;
    }
    
    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }
    
    public String getGoods_ids() {
        return goods_ids;
    }
    
    public void setGoods_ids(String goods_ids) {
        this.goods_ids = goods_ids;
    }
    
    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }
    
    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }
    
    public ActivityGiftManager getActivityGiftManager() {
        return activityGiftManager;
    }
    
    public void setActivityGiftManager(ActivityGiftManager activityGiftManager) {
        this.activityGiftManager = activityGiftManager;
    }

    
    public Integer getStoreId() {
        return storeId;
    }

    
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
    
    public Integer getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
    
    public Integer getGoodsId() {
        return goodsId;
    }
    
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
    
    public GoodsManager getGoodsManager() {
        return goodsManager;
    }
    
    public void setGoodsManager(GoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
    
}
