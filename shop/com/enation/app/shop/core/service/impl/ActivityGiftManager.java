package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.ActivityGift;
import com.enation.eop.sdk.database.BaseSupport;

public class ActivityGiftManager extends BaseSupport<ActivityGift> {

    @Transactional
    public void add(Integer activityId, String goodsIds) {
        String[] goodsArray = goodsIds.split(",");
        ActivityGift activityGift = new ActivityGift();
        String sql = "delete from es_activity_gift where goods_id=?";
        
        for (int i = 0; i < goodsArray.length; i++) {
            //先删除activity_goods表里的goods在添加新的活动的商品
            this.baseDaoSupport.execute(sql, goodsArray[i]);
            
            activityGift.setActivity_id(activityId);
            activityGift.setGoods_id(NumberUtils.toInt(goodsArray[i]));
            this.baseDaoSupport.insert("activity_gift", activityGift);
        }
    }
    
    @Transactional
    public void delete(Integer activityId, String goodsIds) {
        String[] goodsArray = goodsIds.split(",");
        String sql = "delete from es_activity_gift where goods_id=?";
        
        for (int i = 0; i < goodsArray.length; i++) {
            this.baseDaoSupport.execute(sql, goodsArray[i]);
        }
    }
    
    public String getByActivityId(long activityId) {
        String sql = "select wm_concat(id) as id from es_activity_gift where activity_id=?";
        List<Map> list = this.baseDaoSupport.queryForList(sql, activityId);
        Object object = list.get(0).get("id");
        
        if (object == null) {
            return null;
        } else {
            return object.toString();
        }
    }
    
    public Map getNameByActivityId(long activityId, int storeId) {
        String sql = "select wm_concat(g.name) as gift_name, wm_concat(g.goods_id) as gift_id from es_activity_gift agt "
                + "left join es_goods g on agt.goods_id=g.goods_id where activity_id=? and g.store_id=?";
        List<Map> list = this.baseDaoSupport.queryForList(sql, activityId, storeId);
        Map map = list.get(0);
        
        if (map == null) {
            return null;
        } else {
            return map;
        }
    }
    
}
