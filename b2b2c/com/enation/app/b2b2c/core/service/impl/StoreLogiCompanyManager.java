package com.enation.app.b2b2c.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreLogiCompanyManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.database.IDaoSupport;
@Component
public class StoreLogiCompanyManager implements IStoreLogiCompanyManager {
	private IDaoSupport daoSupport;
	private IStoreMemberManager storeMemberManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreLogiCompanyManager#list()
	 */
	@Override
	public List list() {
		StoreMember member =storeMemberManager.getStoreMember();
		String sql="SELECT * from es_logi_company l  LEFT JOIN (select * from es_store_logi_rel s where store_id=? ) s ON l.id=s.logi_id";
		return this.daoSupport.queryForList(sql,member.getStore_id());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreLogiCompanyManager#listByStore()
	 */
	@Override
	public List listByStore() {
		StoreMember member =storeMemberManager.getStoreMember();
		String sql="SELECT * from es_logi_company l  LEFT JOIN (select logi_id from es_store_logi_rel s WHERE s.store_id=? ) s ON l.id=s.logi_id  ";
		return this.daoSupport.queryForList(sql,member.getStore_id());
	}
	@Override
	public void addRel(Integer logi_id) {
		StoreMember member =storeMemberManager.getStoreMember();
		Map map=new HashMap();
		map.put("logi_id", logi_id);
		map.put("store_id", member.getStore_id());
		this.daoSupport.insert("es_store_logi_rel", map);
	}

	@Override
	public void deleteRel(Integer logi_id) {
		StoreMember member =storeMemberManager.getStoreMember();
		String sql="delete from es_store_logi_rel where store_id=? and logi_id=?";
		this.daoSupport.execute(sql, member.getStore_id(),logi_id);
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	
}
