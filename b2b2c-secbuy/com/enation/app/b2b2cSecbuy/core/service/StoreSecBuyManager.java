package com.enation.app.b2b2cSecbuy.core.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2cSecbuy.core.model.StoreSecBuy;
import com.enation.app.b2b2cSecbuy.core.service.impl.IStoreSecBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class StoreSecBuyManager implements IStoreSecBuyManager {
	private IDaoSupport daoSupport;
	private IStoreGoodsManager storeGoodsManager;
	@Override
	public Page listByStoreId(int page, int pageSize, int storeid, Map params) {
		
		String gb_name = (String)params.get("gb_name");
		String gb_status =  (String)params.get("gb_status");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.*,a.act_name,a.start_time,a.end_time from es_secbuy_goods g ,es_secbuy_active a where g.store_id= ? and  g.act_id= a.act_id ");
		
		if(!StringUtil.isEmpty(gb_name)){
			sql.append(" and g.gb_name like '%"+gb_name+"%'");
		}
		if(!StringUtil.isEmpty(gb_status)){
			sql.append(" and g.gb_status="+gb_status);
		}
		sql.append(" order by g.add_time ");
		
		return this.daoSupport.queryForPage(sql.toString(),page,pageSize, storeid);
	}
	@Override
	public StoreSecBuy getBuyGoodsId(int goodsId,int act_id) {
		String sql="SELECT * from es_secbuy_goods WHERE goods_id=? and act_id=? AND ROWNUM<=1";
		StoreSecBuy secBuy= (StoreSecBuy)this.daoSupport.queryForObject(sql, StoreSecBuy.class, goodsId,act_id);
		if (secBuy != null)	secBuy.setStoreGoods(storeGoodsManager.getGoods(secBuy.getGoods_id()));
		return  secBuy;
	}
	@Override
	public StoreSecBuy get(int gbid) {
		String sql="SELECT * from es_secbuy_goods WHERE gb_id=?";
		StoreSecBuy secBuy= (StoreSecBuy)this.daoSupport.queryForObject(sql, StoreSecBuy.class,gbid);
		secBuy.setStoreGoods(storeGoodsManager.getGoods(secBuy.getGoods_id()));
		return  secBuy;
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
