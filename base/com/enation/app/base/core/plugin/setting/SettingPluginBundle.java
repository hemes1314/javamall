package com.enation.app.base.core.plugin.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;



/**
 * 系统设置插件桩
 * @author apexking
 *
 */
public class SettingPluginBundle extends AutoRegisterPluginsBundle {
	
 
	protected static final Log loger = LogFactory
			.getLog(SettingPluginBundle.class);



	
	public String getName() {
		return "系统设置插件桩";
	}



	
	public void registerPlugin(IPlugin plugin) {
		super.registerPlugin(plugin);
	}

	public Map<Integer,String> onInputShow(Map<String,Map<String,String>> settings){
		 
		 Map<Integer,String> htmlMap = new TreeMap<Integer,String>();
	 
		FreeMarkerPaser freeMarkerPaser =  FreeMarkerPaser.getInstance();
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOnSettingInputShow){
					IOnSettingInputShow event = (IOnSettingInputShow)plugin;
					String groupname = event.getSettingGroupName();
					String pageName = event.onShow();
					
					freeMarkerPaser.setClz(event.getClass());
					freeMarkerPaser.setPageName(pageName);
					freeMarkerPaser.putData(groupname, settings.get(groupname));
					htmlMap.put(event.getOrder(), freeMarkerPaser.proessPageContent());
					 
				}
			}
		}
		return htmlMap;
	}

	
	
	
	public Map<Integer,String> getTabs(){
		Map<Integer,String> tabMap = new TreeMap<Integer, String>();
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOnSettingInputShow){
					IOnSettingInputShow event = (IOnSettingInputShow)plugin;
					String name = event.getTabName();
					tabMap.put( event.getOrder(), name);
				}
			}
		}
		
		return tabMap;
	}


	/**
	 * 激发保存事件
	 */
	public void onSave(){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOnSettingSaveEnvent){
					IOnSettingSaveEnvent event = (IOnSettingSaveEnvent)plugin;
					event.onSave();
				}
			}
		}
		
	}

	

 
	
}
