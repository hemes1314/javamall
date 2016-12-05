package com.enation.app.base.core.service.impl;

import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.SettingRuntimeException;
import com.enation.framework.cache.ICache;

/**
 * 设置缓存代理类。
 * @author kingapex
 * 2010-1-15下午03:12:29
 */
public class SettingCacheProxy implements ISettingService {

	private ISettingService settingService;
	private ICache<Map<String,Map<String,String>>> cache;
	private SettingPluginBundle settingPluginBundle;
	private static final String uKey="setting-cache";
	
	public SettingCacheProxy(){		
	}
	
	
	public SettingCacheProxy(ISettingService settingService){
		this.settingService = settingService;
	}
	
	
	@Override
	public void add(String groupname, String name, String value) {
		this.settingService.add(groupname, name, value);
		Map<String,Map<String,String>> settings  = cache.get(uKey);

		settings= this.settingService.getSetting();
		cache.put(uKey,settings);
		
	}


	@Override
	public void save(String groupname, String name, String value) {
		this.settingService.save(groupname, name, value);
		Map<String,Map<String,String>> settings  = cache.get(uKey);

		settings= this.settingService.getSetting();
		cache.put(uKey,settings);
	
		
	}

	@Override
	public void delete(String groupname) {
		this.settingService.delete(groupname);
		Map<String,Map<String,String>> settings  = cache.get(uKey);
		//未命中，由库中取出设置并压入缓存
		if(settings!=null || settings.size()<=0){
			settings.remove(groupname);		 
		}
		
	}

	
	public Map<String,Map<String ,String>>  getSetting() {
		 
		Map<String,Map<String,String>> settings  = cache.get(uKey);
		//未命中，由库中取出设置并压入缓存
		if(settings==null || settings.size()<=0){
			settings= this.settingService.getSetting();
			cache.put(uKey,settings);
		}
		
		return settings;
	}
  
	
	//保存库同时保存缓存
	
	public void save(Map<String,Map<String,String>> settings ) throws SettingRuntimeException {
		this.settingService.save(settings);
		cache.put(uKey,  settingService.getSetting());
		settingPluginBundle.onSave();
	}
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#save(java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(String groupname, Map<String, String> settings) {
		this.settingService.save(groupname, settings);
		this.cache.put(uKey, settingService.getSetting());
	}
	
	public String getSetting(String group, String code) {
		Map<String,Map<String ,String>> settings  = this.getSetting();
		if(settings==null) return null;
		
		Map<String ,String> setting = settings.get(group);		
		if(setting== null )return null;
		
		return setting.get(code);
	}
	
	@Override
	public Map<String, String> getSetting(String group) {
		Map<String,Map<String ,String>> settings  = this.getSetting();
		if(settings==null) return null;
		
		Map<String ,String> setting = settings.get(group);		
		if(setting== null )return null;
		return setting;
	}

	public void setCache(ICache<Map<String, Map<String, String>>> cache) {
		this.cache = cache;
	}


	public SettingPluginBundle getSettingPluginBundle() {
		return settingPluginBundle;
	}


	public void setSettingPluginBundle(SettingPluginBundle settingPluginBundle) {
		this.settingPluginBundle = settingPluginBundle;
	}





	
	
}
