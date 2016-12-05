package com.enation.app.b2b2c.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreDlyCenter;
import com.enation.app.b2b2c.core.service.IStoreDlyCenterManager;
import com.enation.eop.sdk.database.BaseSupport;

@Component
public class StoreDlyCenterManager extends BaseSupport implements IStoreDlyCenterManager {

	@Override
	public List getDlyCenterList(Integer store_id) {
		String sql = "select * from es_dly_center where store_id=?";
		List list = this.baseDaoSupport.queryForList(sql, store_id);
		return list;
	}

	@Override
	public void addDlyCenter(StoreDlyCenter dlyCenter) {
		this.baseDaoSupport.insert("es_dly_center", dlyCenter);
		
	}

	@Override
	public void editDlyCenter(StoreDlyCenter dlyCenter) {
		this.baseDaoSupport.update("es_dly_center", dlyCenter, " dly_center_id="+dlyCenter.getDly_center_id());
		
	}

	@Override
	public StoreDlyCenter getDlyCenter(Integer store_id, Integer dlyid) {
		String sql  = "select * from es_dly_center where dly_center_id=? and store_id=?";
		List list = this.baseDaoSupport.queryForList(sql, StoreDlyCenter.class, dlyid,store_id);
		StoreDlyCenter dlyCenter = (StoreDlyCenter) list.get(0);
		return dlyCenter;
	}

	@Override
	public void delete(Integer dly_id) {
		String sql = "delete from es_dly_center where dly_center_id="+dly_id;
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public void site_default(Integer dly_id,Integer store_id) {
		
		String sql ="update es_dly_center set disabled='false' where store_id="+store_id;
		String sitesql = "update es_dly_center set disabled='true' where dly_center_id="+dly_id+" and store_id="+store_id;
		
		this.baseDaoSupport.execute(sql);
		this.baseDaoSupport.execute(sitesql);
	}



}
