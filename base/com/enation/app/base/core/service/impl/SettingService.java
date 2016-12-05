package com.enation.app.base.core.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.SettingRuntimeException;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.StringUtil;

/**
 * 系统设置业务类
 * 
 * @author apexking
 * 
 */
public class SettingService extends BaseSupport implements ISettingService  {

	private SettingPluginBundle settingPluginBundle;

 

	@Override
	public void add(String groupname, String name, String value) {
		Map<String,String> params = new HashMap<String, String>();
    	params.put(name, value);
    	JSONObject jsonObject = JSONObject.fromObject( params);
    	this.baseDaoSupport.execute("insert into settings(cfg_value,cfg_group)values(?,?)",jsonObject.toString(),groupname);
		
	}


	@Override
	public void save(String groupname, String name, String value) {
		Map<String,String> params = new HashMap<String, String>();
    	params.put(name, value);
    	JSONObject jsonObject = JSONObject.fromObject( params);
    	this.baseDaoSupport.execute("update settings set cfg_value=? where cfg_group=?",jsonObject.toString(),groupname);
	}
	
	

	@Override
	public void delete(String groupname) {
		this.baseDaoSupport.execute("delete from settings where cfg_group=?", groupname);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.setting.service.ISettingService#save()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void save( Map<String,Map<String,String>> settings) throws SettingRuntimeException {

		Iterator<String> settingkeyItor = settings.keySet().iterator();
		 
		while ( settingkeyItor.hasNext() ) {
			
			String settingKey = settingkeyItor.next();
			JSONObject jsonObject = JSONObject.fromObject( settings.get(settingKey) );
			
			this.baseDaoSupport.execute("update settings set cfg_value=? where cfg_group=?",jsonObject.toString(),settingKey);
			
		}
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#save(java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(String groupname, Map<String, String> settings) {
		 
		JSONObject jsonObject = JSONObject.fromObject(settings );
		this.baseDaoSupport.execute("update settings set cfg_value=? where cfg_group=?",jsonObject.toString(),groupname);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.setting.service.ISettingService#getSetting()
	 */
	public Map<String,Map<String ,String>>  getSetting() {
		String sql = "select * from settings";
		List<Map<String, String>> list = this.baseDaoSupport.queryForList(sql);
		Map<String,Map<String,String>> cfg = new HashMap();
		
		for (Map<String,String> map : list) {
			String setting_value = map.get("cfg_value");
			if(StringUtil.isEmpty(setting_value)){
				cfg.put( map.get("cfg_group"), new HashMap<String, String>());
			}else{
				JSONObject jsonObject = JSONObject.fromObject( setting_value );  
				Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
				cfg.put( map.get("cfg_group"), itemMap);
			}
			
		}
		
		return cfg;
	}
	

	public SettingPluginBundle getSettingPluginBundle() {
		return settingPluginBundle;
	}

	public void setSettingPluginBundle(SettingPluginBundle settingPluginBundle) {
		this.settingPluginBundle = settingPluginBundle;
	}
	
	
	public static void main(String[] args){
		String setting_value = "{\"thumbnail_pic_height\":\"40\",\"thumbnail_pic_width\":\"50\"}" ;
		JSONObject jsonObject = JSONObject.fromObject( setting_value );  
		Map map1 = (Map)jsonObject.toBean(jsonObject, Map.class);
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




 

}
