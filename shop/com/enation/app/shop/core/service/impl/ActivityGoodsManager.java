package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.pagecreator.plugin.GoodsCreatorPlugin;
import com.enation.app.shop.core.model.Activity;
import com.enation.app.shop.core.model.ActivityGoods;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.database.BaseSupport;

public class ActivityGoodsManager extends BaseSupport<Activity> {
    
    private IGoodsManager goodsManager;
    private GoodsCreatorPlugin goodsCreatorPlugin;
    
    public List<Activity> getActivityGoodsList() {
    	long currentTime = System.currentTimeMillis() / 1000;
         String sql = "";
         sql +="SELECT ag.*, a.*, g.name as gift_product FROM es_activity_goods ag "
         			+ " left join es_activity a on ag.activity_id=a.id"
         			+ " left join es_goods g on g.goods_id=agt.goods_id  "
         			+ " where a.start_time<" +currentTime
         			+ " and a.end_time > "+currentTime  ;
        List<Activity> activitygoods= this.daoSupport.queryForList(sql,Activity.class);
        return activitygoods;
    }

    public Map getActivityByGoods(Integer goods_id) {
    	long currentTime = System.currentTimeMillis() / 1000;
        String sql = "select ag.*, a.*, g.store_id from es_activity_goods ag "
        			+ "left join es_activity a on ag.activity_id=a.id "
        			+ "left join es_goods g on g.goods_id=ag.goods_id "
        			+ " where a.start_time<" +currentTime
        			+ " and a.end_time > "+currentTime  
        			+ " and ag.goods_id=? and a.is_enable=1";
      
        try {
            Map activitygoods= this.daoSupport.queryForMap(sql,goods_id);
            return activitygoods;
        } catch(Exception e) {
            return null;
        }
    }
    
    /**
     * 通过goodis获得 活动
     * @param goodsId
     * @return
     */
    public Activity getActivityByGoodsId(Integer goodsId) {
        long currentTime = System.currentTimeMillis() / 1000;
        String sql = "select a.* from es_activity_goods ag "
                    + "left join es_activity a on ag.activity_id=a.id "
                    + "left join es_goods g on g.goods_id=ag.goods_id "
                    + " where a.start_time<" +currentTime
                    + " and a.end_time > "+currentTime  
                    + " and ag.goods_id=? and a.is_enable=1";
      
        try {
            return this.daoSupport.queryForObject(sql, Activity.class, goodsId);
        } catch(Exception e) {
            return null;
        }
    }
    
    //chcek 商品是否存在促销
	public int checkGoods(int goods_id) {
		long currentTime = System.currentTimeMillis() / 1000;
		String sql = "SELECT COUNT(*)  FROM es_activity_goods ag "
    			+ " left join es_activity a on ag.activity_id=a.id "
    			+ " left join es_activity_gift agt on agt.activity_id =a.id "
    			+ " left join es_goods g on g.goods_id=agt.goods_id  "
    			+ " where a.start_time<" +currentTime
    			+ " and a.end_time > "+currentTime  
    			+ " and ag.goods_id=? and a.is_enable=1";

		return this.baseDaoSupport.queryForInt(sql, goods_id);
	}
	
	@Transactional
    public void add(Integer activityId, String goodsIds) {
        String[] goodsArray = goodsIds.split(",");
        String sql = "delete from es_activity_goods where goods_id=?";
        ActivityGoods activityGoods = new ActivityGoods();
        
        for (int i = 0; i < goodsArray.length; i++) {
            //先删除activity_goods表里的goods在添加新的活动的商品
            this.baseDaoSupport.execute(sql, goodsArray[i]);
            
            activityGoods.setActivity_id(activityId);
            activityGoods.setGoods_id(NumberUtils.toInt(goodsArray[i]));
            this.baseDaoSupport.insert("activity_goods", activityGoods);
        }
        //重新生成商品静态页
        goodsCreatorPlugin.createGoodsPageForArr(goodsArray);
    }

	@Transactional
	public void delete(Integer activityId, String goodsIds) {
	    String[] goodsArray = goodsIds.split(",");
	    String sql = "delete from es_activity_goods where goods_id=?";
	    
	    for (int i = 0; i < goodsArray.length; i++) {
	        this.baseDaoSupport.execute(sql, goodsArray[i]);
	        //重新生成商品静态页
	        goodsCreatorPlugin.createGoodsPage(goodsManager.get(Integer.valueOf(goodsArray[i])));
	    }
	}
    
    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }
    
    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
    
    public GoodsCreatorPlugin getGoodsCreatorPlugin() {
        return goodsCreatorPlugin;
    }
    
    public void setGoodsCreatorPlugin(GoodsCreatorPlugin goodsCreatorPlugin) {
        this.goodsCreatorPlugin = goodsCreatorPlugin;
    }
	
}