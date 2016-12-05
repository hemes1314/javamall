package com.enation.eop.resource.impl;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IDaoSupport;

/**
 * 后台主题管理
 * 
 * @author kingapex 2010-5-9下午07:46:18
 */
public class AdminThemeManagerImpl extends BaseSupport<AdminTheme> implements IAdminThemeManager {

	private  ISettingService settingService;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(AdminTheme theme, boolean isCommon) {
		try {
			// this.copy(theme,isCommon);
			this.baseDaoSupport.insert("admintheme", theme);
			return this.baseDaoSupport.getLastId("admintheme");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("安装后台主题出错");
		}
	}

	public void clean() {
		this.baseDaoSupport.execute("truncate table admintheme");
	}
 

	public AdminTheme get(Integer themeid) {
		List<AdminTheme> list = this.baseDaoSupport.queryForList("select * from admintheme where id=?", AdminTheme.class, themeid);
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IAdminThemeManager#changeTheme(int)
	 */
	@Override
	public void changeTheme(int themeid) {
		Map map =  this.settingService.getSetting(EopSite.SITE_SETTING_KEY);
		map.put("adminthemeid",""+ themeid);
		this.settingService.save(EopSite.SITE_SETTING_KEY, map);
		EopSite.reload();
	}
	
	
	public List<AdminTheme> list() {
		return this.baseDaoSupport.queryForList("select * from admintheme ", AdminTheme.class);
	}

	public IDaoSupport<AdminTheme> getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport<AdminTheme> daoSupport) {
		this.daoSupport = daoSupport;
	}


	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}
	
	
}
