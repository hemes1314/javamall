package com.enation.app.b2b2ccostdown.core.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2ccostdown.core.model.StoreCostDown;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

@Component
public class StoreCostDownManager {

    private IDaoSupport daoSupport;

    private IStoreGoodsManager storeGoodsManager;

    public Page listByStoreId(int page, int pageSize, int storeid, Map params) {

        String gb_name = (String) params.get("gb_name");
        String gb_status = (String) params.get("gb_status");

        StringBuffer sql = new StringBuffer();
        sql.append("select g.*,a.act_name,a.start_time,a.end_time from es_cost_down_goods g ,es_cost_down_active a where g.store_id= ? and  g.act_id= a.act_id ");

        if(!StringUtil.isEmpty(gb_name)) {
            sql.append(" and g.gb_name like '%" + gb_name + "%'");
        }
        if(!StringUtil.isEmpty(gb_status)) {
            sql.append(" and g.gb_status=" + gb_status);
        }
        sql.append(" order by g.add_time ");

        return this.daoSupport.queryForPage(sql.toString(), page, pageSize, storeid);
    }

    public StoreCostDown getBuyGoodsId(int goodsId, int actId) {
    	return getBuyGoodsId(goodsId, actId, null);
    }
    
    public StoreCostDown getBuyGoodsId(int goodsId, int actId, Integer gbStatus) {
    	
    	int status = 1;
    	if(gbStatus != null) {
    		status = 0;
    	}
    	
    	String sql = "SELECT * from es_cost_down_goods WHERE goods_id=? and GB_STATUS=? and act_id=? AND ROWNUM<=1";
        StoreCostDown groupBuy = (StoreCostDown) this.daoSupport.queryForObject(sql.toString(), StoreCostDown.class, goodsId, status,
                actId);
        if(groupBuy != null) groupBuy.setStoreGoods(storeGoodsManager.getGoods(groupBuy.getGoods_id()));
        return groupBuy;
    }

    public StoreCostDown get(int gbId) {
        String sql = "SELECT * from es_cost_down_goods WHERE gb_id=?";
        StoreCostDown groupBuy = (StoreCostDown) this.daoSupport.queryForObject(sql, StoreCostDown.class, gbId);
        groupBuy.setStoreGoods(storeGoodsManager.getGoods(groupBuy.getGoods_id()));
        return groupBuy;
    }

    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    public IStoreGoodsManager getStoreGoodsManager() {
        return storeGoodsManager;
    }

    public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
        this.storeGoodsManager = storeGoodsManager;
    }

}
