package com.enation.app.advbuy.core.service.impl;


import com.enation.framework.cache.CacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.app.groupbuy.core.service.IGroupBuyManager;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
@Component
public class AdvBuyManager implements IAdvBuyManager{
	private IDaoSupport daoSupport;
	private IAdvBuyActiveManager advBuyActiveManager;
	private IGoodsManager goodsManager;
	
	@Autowired
	private IGroupBuyManager groupBuyManager;
	@Autowired
    private ISecBuyManager secBuyManager;
	@Autowired
    private IFlashBuyManager flashBuyManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#add(com.enation.app.shop.component.advbuy.model.AdvBuy)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(AdvBuy advBuy) {
	    int goodsId = advBuy.getGoods_id();
        flashBuyManager.deleteByGoodsId(goodsId);
        secBuyManager.deleteByGoodsId(goodsId);
        groupBuyManager.deleteByGoodsId(goodsId);
        this.deleteByGoodsId(goodsId);
        
		advBuy.setAdd_time(DateUtil.getDateline());
		this.daoSupport.insert("es_advbuy_goods", advBuy);
		return this.daoSupport.getLastId("es_advbuy_goods");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#update(com.enation.app.shop.component.advbuy.model.AdvBuy)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AdvBuy advBuy) {
		this.daoSupport.update("es_advbuy_goods", advBuy, "gb_id="+advBuy.getGb_id());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#delete(int)
	 */
	@Override
	public void delete(int gbid) {
		String sql="delete from  es_advbuy_goods where gb_id=?";
		this.daoSupport.execute(sql, gbid);
	}
	
	@Override
    public void deleteByGoodsId(int goodsId) {
        String sql="delete from  es_advbuy_goods where goods_id=?";
        this.daoSupport.execute(sql, goodsId);
        
        sql="update es_goods set is_advbuy=0 where goods_id=?";
        this.daoSupport.execute(sql, goodsId);
        //hp清除缓存
        com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsId));

	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#auth(int, int)
	 */
	@Override
	public void auth(int gbid, int status) {
		String sql="update es_advbuy_goods set gb_status=? where gb_id=?";
		this.daoSupport.execute(sql, status,gbid);
		//获取审核的预售
		//更改商品为预售商品
		AdvBuy advBuy=(AdvBuy)this.daoSupport.queryForObject("select * from es_advbuy_goods where gb_id=?", AdvBuy.class, gbid);
		if(advBuyActiveManager.get(advBuy.getAct_id()).getAct_status()==1){
//		    sql = "update es_goods set is_cost_down=0, is_groupbuy = 0,is_flashbuy=0,is_secbuy=0,is_advbuy=1 where goods_id=?";
		    sql = "update es_goods set is_advbuy=1 where goods_id=?";
			this.daoSupport.execute(sql, advBuy.getGoods_id());
			goodsManager.startChange(goodsManager.get(advBuy.getGoods_id()));
			  //hp清除缓存
            com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(advBuy.getGoods_id()));

		}
				
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#listByActId(int, int, int, java.lang.Integer)
	 */
	@Override
	public Page listByActId(int page, int pageSize, int actid, Integer status) {
		StringBuffer sql = new StringBuffer();
		sql.append("select g.*,a.act_name,a.start_time,a.end_time from es_advbuy_goods g ,es_advbuy_active a ");
		sql.append(" where g.act_id= ? and  g.act_id= a.act_id");
		if(status!=null ){
			sql.append(" and g.gb_status="+status);
		}
		sql.append(" order by g.add_time ");
		return this.daoSupport.queryForPage(sql.toString(),page,pageSize, actid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#search(int, int, java.lang.Integer, java.lang.Double, java.lang.Double, int, int, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page search(int page, int pageSize, Integer catid, Double minprice,
			Double maxprice, Integer sort_key, Integer sort_type, Integer search_type,Integer area_id) {
		StringBuffer sql= new StringBuffer("select b.* from es_advbuy_goods b inner join es_goods g on g.goods_id=b.goods_id where b.gb_status=1 and g.market_enable=1 and g.disabled=0 ");
		if(catid!=0){
		sql.append(" and b.cat_id="+catid);
		}
		
		if(minprice!=null){
			sql.append(" and b.price>="+minprice);
		}
		
		
		if(maxprice!=null&&maxprice!=0){
			sql.append(" and b.price<="+maxprice);
		}
		
		if(sort_type==0){
			sql.append(" and b.act_id in (select act_id from es_advbuy_active where end_time <"+DateUtil.getDateline()+")");
		}
	
		//
		if(sort_type==1&&catid==0){
		    AdvBuyActive curAct = advBuyActiveManager.get();
            int curActId = curAct == null ? 0 : curAct.getAct_id();
            sql.append(" and b.act_id="+curActId);
		}
		
		if(sort_type==1&&catid!=0){
		    AdvBuyActive curAct = advBuyActiveManager.get();
            int curActId = curAct == null ? 0 : curAct.getAct_id();
            sql.append(" and b.act_id="+curActId+" and b.cat_id="+catid);
		}
		if(sort_type==2&&catid!=0){
			sql.append(" and b.act_id in (select act_id from es_advbuy_active where start_time >"+DateUtil.getDateline()+")");
		}
		
		if(sort_type==2&&catid==0){
			sql.append(" and b.act_id in (select act_id from es_advbuy_active where start_time >"+DateUtil.getDateline()+")");
		}
		if(area_id!=0){
			sql.append(" and b.area_id="+area_id);
		}
		if(sort_key==0){
			sql.append(" order by b.add_time ");
		}
		
		if(sort_key==1){
			sql.append(" order by b.price ");
		}
		
		if(sort_key==2){
			sql.append(" order by b.price/b.original_price ");
		}
		
		if(sort_key==3){
			sql.append(" order by b.buy_num+b.visual_num desc ");
		}
		
		//System.out.println(sql.toString());
		return this.daoSupport.queryForPage(sql.toString(), page, pageSize);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#get(int)
	 */
	@Override
	public AdvBuy get(int gbid) {
		String sql ="select * from es_advbuy_goods where gb_id=?";
		AdvBuy advBuy = (AdvBuy)this.daoSupport.queryForObject(sql, AdvBuy.class, gbid);
		sql="select * from es_goods where goods_id=?";
		
		Goods goods  = (Goods)this.daoSupport.queryForObject(sql, Goods.class, advBuy.getGoods_id());
		advBuy.setGoods(goods);
		
		return advBuy;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.advbuy.service.IAdvBuyManager#getBuyGoodsId(int)
	 */
	@Override
	public AdvBuy getBuyGoodsId(int goodsId) {
	    AdvBuyActive advBuyAct = advBuyActiveManager.get();
        if (advBuyAct == null) return null;
        
		String sql="SELECT * from es_advbuy_goods WHERE goods_id=? and act_id=? AND gb_status=1";
		AdvBuy advBuy= (AdvBuy)this.daoSupport.queryForObject(sql, AdvBuy.class, goodsId,advBuyAct.getAct_id());
		
		if (advBuy != null) advBuy.setGoods((Goods)this.daoSupport.queryForObject("select * from es_goods where goods_id=?", Goods.class, advBuy.getGoods_id()));
		return  advBuy;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IAdvBuyActiveManager getAdvBuyActiveManager() {
		return advBuyActiveManager;
	}

	public void setAdvBuyActiveManager(
			IAdvBuyActiveManager advBuyActiveManager) {
		this.advBuyActiveManager = advBuyActiveManager;
	}

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
}
