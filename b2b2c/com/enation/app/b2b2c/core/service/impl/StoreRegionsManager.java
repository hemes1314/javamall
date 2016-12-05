package com.enation.app.b2b2c.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreRegionsManager;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 地区 manager
 * @author xulipeng
 */
@Component
public class StoreRegionsManager extends BaseSupport implements IStoreRegionsManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreRegionsManager#getRegionsToAreaList()
	 */
	@Override
	public List getRegionsToAreaList() {
		String sql ="select * from es_regions where p_region_id=?";
		List<Map> list = this.baseDaoSupport.queryForList(sql,0);
		for(Map map :list){
			Integer regionid = (Integer) map.get("region_id");
			List arealist = this.baseDaoSupport.queryForList(sql, regionid);
			map.put("arealist", arealist);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreRegionsManager#getRegionsbyids(java.lang.String)
	 */
	@Override
	public List getRegionsbyids(String ids) {
		String sql = "select * from es_regions where region_id in ("+ids+")";
		List list = this.baseDaoSupport.queryForList(sql);
		return list;
	}
	
}
