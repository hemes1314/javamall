package com.enation.app.base.core.plugin.user;

import java.util.ArrayList;
import java.util.List;

import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 管理员插件桩
 * 
 * @author kingapex
 * 
 */
public class AdminUserPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		return "管理员插件桩";
	}

	
	/**
	 * 激发攻取添加/修改页面html事件
	 * @param user
	 * @return
	 */
	public List<String> getInputHtml(AdminUser user) {
		List<String> list  = new ArrayList<String>();
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
			
			for (IPlugin plugin : plugins) {
					if(plugin instanceof IAdminUserInputDisplayEvent){
						IAdminUserInputDisplayEvent event = (IAdminUserInputDisplayEvent)plugin;
						freeMarkerPaser.setClz(event.getClass());
						String html = event.getInputHtml(user);
						list.add(html);
					}
			}

		}
		return list;
	}
	
	
	
	/**
	 * 激发添加事件
	 * @param userid
	 */
	public void onAdd(Long userid){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			
			for (IPlugin plugin : plugins) {
					if(plugin instanceof IAdminUserOnAddEvent){
						IAdminUserOnAddEvent event = (IAdminUserOnAddEvent)plugin;
						event.onAdd(userid);
						 
					}
			}

		}
	}
	
	
	/**
	 * 激发修改事件
	 * @param userid
	 */
	public void onEdit(Long userid){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			
			for (IPlugin plugin : plugins) {
					if(plugin instanceof IAdminUserOnEditEvent){
						IAdminUserOnEditEvent event = (IAdminUserOnEditEvent)plugin;
						event.onEdit(userid);
					}
			}

		}
	}
	
	
	/**
	 * 激发修改事件
	 * @param userid
	 */
	public void onDelete(Long userid){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			
			for (IPlugin plugin : plugins) {
					if(plugin instanceof IAdminUserDeleteEvent){
						IAdminUserDeleteEvent event = (IAdminUserDeleteEvent)plugin;
						event.onDelete(userid);
					}
			}

		}
	}
	
	
	public void onLogin(AdminUser user){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			
			for (IPlugin plugin : plugins) {
					if(plugin instanceof IAdminUserLoginEvent){
						IAdminUserLoginEvent event = (IAdminUserLoginEvent)plugin;
						event.onLogin(user);
					}
			}

		}
	}
	
	
}
