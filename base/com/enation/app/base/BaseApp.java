package com.enation.app.base;

import com.enation.app.base.core.service.impl.cache.SiteMenuCacheProxy;
import com.enation.eop.resource.impl.cache.ThemeUriCacheProxy;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.App;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.ISqlFileExecutor;

/**
 * base应用
 * 
 * @author kingapex 2010-9-20下午04:36:26
 */
public class BaseApp extends App {
	private IDBRouter baseDBRouter;
	private ISqlFileExecutor sqlFileExecutor;

	public BaseApp() {
		
		tables.add("adv");
	//	tables.add("access");
		tables.add("adcolumn");
		tables.add("admintheme");		
		tables.add("friends_link");
		tables.add("guestbook");
		tables.add("menu");
		tables.add("theme");
		tables.add("themeuri");			
		tables.add("settings");
		tables.add("site_menu");		
		tables.add("smtp");
		//tables.add("regions");//lzf add 20120308
	}

	/************** 应用的一些基础信息 ***********/
	public String getId() {
		return "base";
	}

	public String getName() {
		return "base应用";
	}

	public String getNameSpace() {
		return "/core";
	}
 
  


	/**
	 * 系统初始化安装时安装base的sql脚本
	 */
	public void install() {
		this.doInstall("file:com/enation/app/base/base.xml");
	}

	protected void cleanCache() {
	 
		// 清除挂件缓存
		CacheFactory.getCache(CacheFactory.WIDGET_CACHE_NAME_KEY).remove(
				"widget_" + userid + "_" + siteid);

	 
		// 清除themuri缓存
		CacheFactory.getCache(CacheFactory.THEMEURI_CACHE_NAME_KEY).remove(
				ThemeUriCacheProxy.LIST_KEY_PREFIX + userid + "_" + siteid);

		// 清除SiteMenu缓存
		CacheFactory.getCache(SiteMenuCacheProxy.MENU_LIST_CACHE_KEY).remove(
				SiteMenuCacheProxy.MENU_LIST_CACHE_KEY + "_" + userid + "_"
						+ siteid);

	}

	/**
	 * session失效事件
	 */
	public void sessionDestroyed(String seesionid, EopSite site) {
		// do noting
	}

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}

	public IDBRouter getBaseSaasDBRouter() {
		return baseDBRouter;
	}

	public void setBaseSaasDBRouter(IDBRouter baseSaasDBRouter) {
		this.baseDBRouter = baseSaasDBRouter;
	}

	public ISqlFileExecutor getSqlFileExecutor() {
		return sqlFileExecutor;
	}

	public void setSqlFileExecutor(ISqlFileExecutor sqlFileExecutor) {
		this.sqlFileExecutor = sqlFileExecutor;
	}
 

}
