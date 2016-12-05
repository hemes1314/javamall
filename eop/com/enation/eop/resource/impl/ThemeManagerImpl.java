package com.enation.eop.resource.impl;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Theme;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.FileUtil;

public class ThemeManagerImpl extends BaseSupport<Theme> implements
		IThemeManager {
	
	private  ISettingService settingService;
	
	public void clean() {
		this.baseDaoSupport.execute("truncate table theme");
	}

	public Theme getTheme(Integer themeid) {
		return this.baseDaoSupport.queryForObject("select * from theme where id=?", Theme.class, themeid);
	}

	public List<Theme> list() {
			return this.baseDaoSupport.queryForList("select * from theme where siteid = 0", Theme.class);
		 
	}

	/*
	 * 取得主站的theme列表 (non-Javadoc)
	 * 
	 * @see com.enation.eop.core.resource.IThemeManager#getMainThemeList()
	 */
	public List<Theme> list(int siteid) {
		return this.baseDaoSupport.queryForList("select * from theme where siteid = 0", Theme.class);
	}

	public void addBlank(Theme theme) {
		try {

			this.baseDaoSupport.insert("theme", theme);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("创建主题出错");
		}
	}

	public Integer add(Theme theme, boolean isCommon) {
		try {
			//4.0开始不再copy目录 
		//	this.copy(theme, isCommon);
			this.baseDaoSupport.insert("theme", theme);
			return this.baseDaoSupport.getLastId("theme");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("安装主题出错");
		}
	}

	@Override
	public void changetheme(int themeid) {
		 Theme theme = this.getTheme(themeid);
			Map map =  this.settingService.getSetting(EopSite.SITE_SETTING_KEY);
			map.put("themeid",""+ themeid);
			map.put("themepath", theme.getPath());
			this.settingService.save(EopSite.SITE_SETTING_KEY, map);
			EopSite.reload();
		 
	}

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	
	
 
}
