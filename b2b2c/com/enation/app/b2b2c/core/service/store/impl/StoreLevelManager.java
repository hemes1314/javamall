package com.enation.app.b2b2c.core.service.store.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class StoreLevelManager extends BaseSupport implements IStoreLevelManager{

	@Override
	public List storeLevelList() {
		String sql="select * from es_store_level";
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public void addStoreLevel(String levelName) {
		StoreLevel storeLevel=new StoreLevel();
		storeLevel.setLevel_name(levelName);
		this.daoSupport.insert("es_store_level", storeLevel);
		
	}

	@Override
	public void editStoreLevel(String levelName, Integer levelId) {
		
		String sql="update es_store_level set level_name=? where level_id=?";
		this.daoSupport.execute(sql, levelName,levelId);
	}

	@Override
	public void delStoreLevel(Integer levelId) {
		String sql="DELETE from es_store_level WHERE level_id=?";
		this.daoSupport.execute(sql, levelId);
	}

	@Override
	public StoreLevel getStoreLevel(Integer levelId) {
		String sql="select * from es_store_level where level_id=?";
		return (StoreLevel) this.baseDaoSupport.queryForObject(sql,StoreLevel.class, levelId);
	}


}
