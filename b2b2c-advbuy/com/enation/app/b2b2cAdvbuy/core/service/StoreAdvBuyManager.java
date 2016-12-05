package com.enation.app.b2b2cAdvbuy.core.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2cAdvbuy.core.model.StoreAdvBuy;
import com.enation.app.b2b2cAdvbuy.core.service.impl.IStoreAdvBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class StoreAdvBuyManager implements IStoreAdvBuyManager {
	private IDaoSupport daoSupport;
	private IStoreGoodsManager storeGoodsManager;
	@Override
	public Page listByStoreId(int page, int pageSize, int storeid, Map params) {
		
		String gb_name = (String)params.get("gb_name");
		String gb_status =  (String)params.get("gb_status");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.*,a.act_name,a.start_time,a.end_time from es_advbuy_goods g ,es_advbuy_active a where g.store_id= ? and  g.act_id= a.act_id ");
		
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
	public StoreAdvBuy getBuyGoodsId(int goodsId,int act_id) {
		String sql="SELECT * from es_advbuy_goods WHERE goods_id=? and act_id=? AND ROWNUM<=1";
		StoreAdvBuy advBuy= (StoreAdvBuy)this.daoSupport.queryForObject(sql, StoreAdvBuy.class, goodsId,act_id);
		if (advBuy != null) advBuy.setStoreGoods(storeGoodsManager.getGoods(advBuy.getGoods_id()));
		return  advBuy;
	}
	@Override
	public StoreAdvBuy get(int gbid) {
		String sql="SELECT * from es_advbuy_goods WHERE gb_id=?";
		StoreAdvBuy advBuy= (StoreAdvBuy)this.daoSupport.queryForObject(sql, StoreAdvBuy.class,gbid);
		advBuy.setStoreGoods(storeGoodsManager.getGoods(advBuy.getGoods_id()));
		return  advBuy;
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
