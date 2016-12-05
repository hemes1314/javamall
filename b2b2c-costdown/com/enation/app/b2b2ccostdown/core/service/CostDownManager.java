package com.enation.app.b2b2ccostdown.core.service;

import com.enation.framework.cache.CacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 直降
 * @author Jeffrey
 *
 */
@Component
public class CostDownManager {

    private IDaoSupport daoSupport;

    @Autowired
    private CostDownActiveManager costDownActiveManager;

    private IGoodsManager goodsManager;

    /*
     * (non-Javadoc)
     * @see com.enation.app.shop.component.groupbuy.service.IGroupBuyManager#add(com.enation.app.shop.component.groupbuy.model.GroupBuy)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int add(CostDown cb) {
        int goodsId = cb.getGoods_id();
        this.deleteByGoodsId(goodsId);
        cb.setAdd_time(DateUtil.getDateline());
        this.daoSupport.insert("es_cost_down_goods", cb);
        return this.daoSupport.getLastId("es_cost_down_goods");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(CostDown cb) {
        this.daoSupport.update("es_cost_down_goods", cb, "gb_id=" + cb.getGb_id());
    }

    public void delete(int gbId) {
        String sql = "delete from  es_cost_down_goods where gb_id=?";
        this.daoSupport.execute(sql, gbId);
    }

    public void deleteByGoodsId(int goodsId) {
        String sql = "delete from  es_cost_down_goods where goods_id=?";
        this.daoSupport.execute(sql, goodsId);

        sql = "update es_goods set is_cost_down=0 where goods_id=?";
        this.daoSupport.execute(sql, goodsId);
        //hp清除缓存
        com.enation.framework.cache.ICache iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsId));

    }

    public void auth(int gbId, int status) {
        String sql = "update es_cost_down_goods set gb_status=? where gb_id=?";
        this.daoSupport.execute(sql, status, gbId);
        //获取审核的团购
        //更改商品为团购商品
        CostDown cost_down = (CostDown) this.daoSupport.queryForObject(
                "select * from es_cost_down_goods where gb_id=?", CostDown.class, gbId);
        if(costDownActiveManager.get(cost_down.getAct_id()).getAct_status() == 1) {
//            sql = "update es_goods set is_cost_down=1, is_groupbuy = 0,is_flashbuy=0,is_secbuy=0,is_advbuy=0 where goods_id=?";
            sql = "update es_goods set is_cost_down=1 where goods_id=?";
            this.daoSupport.execute(sql, cost_down.getGoods_id());
            goodsManager.startChange(goodsManager.get(cost_down.getGoods_id()));
            //hp清除缓存
            com.enation.framework.cache.ICache iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(cost_down.getGoods_id()));

        }

    }

    public Page listByActId(int page, int pageSize, int actid, Integer status) {
        StringBuffer sql = new StringBuffer();
        sql.append("select g.*,a.act_name,a.start_time,a.end_time,a.join_end_time from es_cost_down_goods g ,es_cost_down_active a ");
        sql.append(" where g.act_id= ? and  g.act_id= a.act_id");
        if(status != null) {
            sql.append(" and g.gb_status=" + status);
        }
        sql.append(" order by g.add_time ");
        return this.daoSupport.queryForPage(sql.toString(), page, pageSize, actid);
    }

    public Page search(int page, int pageSize, Integer catid, Double minprice, Double maxprice, Integer sort_key,
            Integer sort_type, Integer search_type, Integer area_id) {
        StringBuffer sql = new StringBuffer(
                "select b.* from es_cost_down_goods b inner join es_goods g on g.goods_id=b.goods_id where b.gb_status=1 and g.market_enable=1 and g.disabled=0 ");
        if(catid != 0) {
            sql.append(" and b.cat_id=" + catid);
        }

        if(minprice != null) {
            sql.append(" and b.price>=" + minprice);
        }

        if(maxprice != null && maxprice != 0) {
            sql.append(" and b.price<=" + maxprice);
        }

        if(sort_type == 0) {
            sql.append(" and b.act_id in (select act_id from es_cost_down_active where end_time <"
                    + DateUtil.getDateline() + ")");
        }

        //
        if(sort_type == 1 && catid == 0) {
            CostDownActive curAct = costDownActiveManager.get();
            int curActId = curAct == null ? 0 : curAct.getAct_id();
            sql.append(" and b.act_id=" + curActId);
        }

        if(sort_type == 1 && catid != 0) {
            CostDownActive curAct = costDownActiveManager.get();
            int curActId = curAct == null ? 0 : curAct.getAct_id();
            sql.append(" and b.act_id=" + curActId + " and b.cat_id=" + catid);
        }
        if(sort_type == 2 && catid != 0) {
            sql.append(" and b.act_id in (select act_id from es_cost_down_active where start_time >"
                    + DateUtil.getDateline() + ")");
        }

        if(sort_type == 2 && catid == 0) {
            sql.append(" and b.act_id in (select act_id from es_cost_down_active where start_time >"
                    + DateUtil.getDateline() + ")");
        }
        if(area_id != 0) {
            sql.append(" and b.area_id=" + area_id);
        }
        if(sort_key == 0) {
            sql.append(" order by b.add_time ");
        }

        if(sort_key == 1) {
            sql.append(" order by b.price ");
        }

        if(sort_key == 2) {
            sql.append(" order by b.price/b.original_price ");
        }

        if(sort_key == 3) {
            sql.append(" order by b.buy_num+b.visual_num desc ");
        }

        //System.out.println(sql.toString());
        return this.daoSupport.queryForPage(sql.toString(), page, pageSize);
    }

    public CostDown get(int gbId) {
        String sql = "select * from es_cost_down_goods where gb_id=?";
        CostDown cost_down = (CostDown) this.daoSupport.queryForObject(sql, CostDown.class, gbId);
        sql = "select * from es_goods where goods_id=?";

        Goods goods = (Goods) this.daoSupport.queryForObject(sql, Goods.class, cost_down.getGoods_id());
        cost_down.setGoods(goods);

        return cost_down;
    }

    public CostDown getBuyGoodsId(int goodsId) {
        CostDownActive cost_downAct = costDownActiveManager.get();
        if(cost_downAct == null) return null;

        String sql = "SELECT * from es_cost_down_goods WHERE goods_id=? and act_id=? AND gb_status=1";
        CostDown cost_down = (CostDown) this.daoSupport.queryForObject(sql, CostDown.class, goodsId,
                cost_downAct.getAct_id());

        if(cost_down != null) cost_down.setGoods((Goods) this.daoSupport.queryForObject(
                "select * from es_goods where goods_id=?", Goods.class, cost_down.getGoods_id()));
        return cost_down;
    }
    
    public CostDown getByActIdGoodsId(int actId, int goodsId) {

        String sql = "SELECT * from es_cost_down_goods WHERE goods_id=? and act_id=? AND gb_status <> 2";
        CostDown cost_down = (CostDown) this.daoSupport.queryForObject(sql, CostDown.class, goodsId, actId);
        return cost_down;
    }

    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
}
