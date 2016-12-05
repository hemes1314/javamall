package com.enation.app.secbuy.core.service.impl;


import com.enation.framework.cache.CacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.app.flashbuy.core.service.IFlashBuyManager;
import com.enation.app.groupbuy.core.service.IGroupBuyManager;
import com.enation.app.secbuy.core.model.SecBuy;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.secbuy.core.service.ISecBuyManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;

import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
@Component
public class SecBuyManager implements ISecBuyManager{
	private IDaoSupport daoSupport;
	private ISecBuyActiveManager secBuyActiveManager;
	private IGoodsManager goodsManager;
	
	@Autowired
	private IGroupBuyManager groupBuyManager;
	@Autowired
	private IFlashBuyManager flashBuyManager;
	@Autowired
	private IAdvBuyManager advBuyManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#add(com.enation.app.shop.component.secbuy.model.SecBuy)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(SecBuy secBuy) {
	    int goodsId = secBuy.getGoods_id();
        flashBuyManager.deleteByGoodsId(goodsId);
        advBuyManager.deleteByGoodsId(goodsId);
        groupBuyManager.deleteByGoodsId(goodsId);
        this.deleteByGoodsId(goodsId);
        
		secBuy.setAdd_time(DateUtil.getDateline());
		this.daoSupport.insert("es_secbuy_goods", secBuy);
		return this.daoSupport.getLastId("es_secbuy_goods");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#update(com.enation.app.shop.component.secbuy.model.SecBuy)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecBuy secBuy) {
		this.daoSupport.update("es_secbuy_goods", secBuy, "gb_id="+secBuy.getGb_id());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#delete(int)
	 */
	@Override
	public void delete(int gbid) {
		String sql="delete from  es_secbuy_goods where gb_id=?";
		this.daoSupport.execute(sql, gbid);
	}
	
	@Override
    public void deleteByGoodsId(int goodsId) {
        String sql="delete from  es_secbuy_goods where goods_id=?";
        this.daoSupport.execute(sql, goodsId);
        
        sql="update es_goods set is_secbuy=0 where goods_id=?";
        this.daoSupport.execute(sql, goodsId);
      //hp清除缓存
        com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsId));

    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#auth(int, int)
	 */
	@Override
	public void auth(int gbid, int status) {
		String sql="update es_secbuy_goods set gb_status=? where gb_id=?";
		this.daoSupport.execute(sql, status,gbid);
		//获取审核的秒拍
		//更改商品为秒拍商品
		SecBuy secBuy=(SecBuy)this.daoSupport.queryForObject("select * from es_secbuy_goods where gb_id=?", SecBuy.class, gbid);
		if(secBuyActiveManager.get(secBuy.getAct_id()).getAct_status()==1){
//		    sql = "update es_goods set is_cost_down=0, is_groupbuy = 0,is_flashbuy=0,is_secbuy=1,is_advbuy=0 where goods_id=?";
		    sql = "update es_goods set is_secbuy=1 where goods_id=?";
			this.daoSupport.execute(sql, secBuy.getGoods_id());
			goodsManager.startChange(goodsManager.get(secBuy.getGoods_id()));
			 //hp清除缓存
            com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(secBuy.getGoods_id()));
    
		}
				
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#listByActId(int, int, int, java.lang.Integer)
	 */
	@Override
	public Page listByActId(int page, int pageSize, int actid, Integer status) {
		StringBuffer sql = new StringBuffer();
		sql.append("select g.*,a.act_name,a.start_time,a.end_time from es_secbuy_goods g ,es_secbuy_active a ");
		sql.append(" where g.act_id= ? and  g.act_id= a.act_id");
		if(status!=null ){
			sql.append(" and g.gb_status="+status);
		}
		sql.append(" order by g.add_time ");
		return this.daoSupport.queryForPage(sql.toString(),page,pageSize, actid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#search(int, int, java.lang.Integer, java.lang.Double, java.lang.Double, int, int, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page search(int page, int pageSize, Integer catid, Double minprice,
			Double maxprice, Integer sort_key, Integer sort_type, Integer search_type,Integer area_id) {
		StringBuffer sql= new StringBuffer("select b.* from es_secbuy_goods b inner join es_goods g on g.goods_id=b.goods_id where b.gb_status=1 and g.market_enable=1 and g.disabled=0 ");
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
			sql.append(" and b.act_id in (select act_id from es_secbuy_active where end_time <"+DateUtil.getDateline()+")");
		}
	
		//
		if(sort_type==1&&catid==0){
		    SecBuyActive curAct = secBuyActiveManager.get();
		    int curActId = curAct == null ? 0 : curAct.getAct_id();
			sql.append(" and b.act_id="+curActId);
		}
		
		if(sort_type==1&&catid!=0){
		    SecBuyActive curAct = secBuyActiveManager.get();
            int curActId = curAct == null ? 0 : curAct.getAct_id();
            sql.append(" and b.act_id="+curActId+" and b.cat_id="+catid);
		}
		if(sort_type==2&&catid!=0){
			sql.append(" and b.act_id in (select act_id from es_secbuy_active where start_time >"+DateUtil.getDateline()+")");
		}
		
		if(sort_type==2&&catid==0){
			sql.append(" and b.act_id in (select act_id from es_secbuy_active where start_time >"+DateUtil.getDateline()+")");
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
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#get(int)
	 */
	@Override
	public SecBuy get(int gbid) {
		String sql ="select * from es_secbuy_goods where gb_id=?";
		SecBuy secBuy = (SecBuy)this.daoSupport.queryForObject(sql, SecBuy.class, gbid);
		sql="select * from es_goods where goods_id=?";
		
		Goods goods  = (Goods)this.daoSupport.queryForObject(sql, Goods.class, secBuy.getGoods_id());
		secBuy.setGoods(goods);
		
		return secBuy;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.secbuy.service.ISecBuyManager#getBuyGoodsId(int)
	 */
	@Override
	public SecBuy getBuyGoodsId(int goodsId) {
	    SecBuyActive secBuyAct = secBuyActiveManager.get();
        if (secBuyAct == null) return null;
        
		String sql="SELECT * from es_secbuy_goods WHERE goods_id=? and act_id=? AND gb_status=1";
		SecBuy secBuy= (SecBuy)this.daoSupport.queryForObject(sql, SecBuy.class, goodsId,secBuyAct.getAct_id());
		
		if (secBuy != null) secBuy.setGoods((Goods)this.daoSupport.queryForObject("select * from es_goods where goods_id=?", Goods.class, secBuy.getGoods_id()));
		return  secBuy;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public ISecBuyActiveManager getSecBuyActiveManager() {
		return secBuyActiveManager;
	}

	public void setSecBuyActiveManager(
			ISecBuyActiveManager secBuyActiveManager) {
		this.secBuyActiveManager = secBuyActiveManager;
	}
	

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
}
