package com.enation.app.base.core.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * @author apexking
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("setting")
@Results({
	@Result(name="input", type="freemarker", location="/core/admin/setting/input.html") 
})
public class SettingAction extends WWAction {
	
	private String showtab;
	
	private ISettingService settingService;
	private SettingPluginBundle settingPluginBundle;
	
	private Map<Integer,String> htmls;
 
	private String[] codes;
	private String[]  cfg_values;
	
	
	private Map tabs;
	
	public Map getTabs(){
		return this.tabs;
	}
	
	public static final String SETTING_PAGE_ID= "setting_input";
	
	/**
	 * 到设置页面
	 */
	public String edit_input(){
		
		Map settings = settingService.getSetting();
		htmls = this.settingPluginBundle.onInputShow(settings);
		tabs = this.settingPluginBundle.getTabs();
			
		return this.INPUT;
	}
	
	/**
	 * 保存配置
	 * @return
	 */
	public String save(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson(EopSetting.DEMO_SITE_TIP);
			return this.JSON_MESSAGE;
		}
		
		
		HttpServletRequest  request = this.getRequest();
		Enumeration<String> names = request.getParameterNames();
		Map<String,Map<String,String>> settings = new HashMap<String, Map<String,String>>();
		
	    while(names.hasMoreElements()){
	    	

	    	String name= names.nextElement();
	    	String[]name_ar = name.split("\\.");
	    	if(name_ar.length!=2) continue;
	    	
	    	String groupName = name_ar[0];
	    	String paramName  = name_ar[1];
	    	String paramValue = request.getParameter(name);
	    	
	    	Map<String,String> params = settings.get(groupName);
	    	if(params==null){
	    		params = new HashMap<String, String>();
	    		settings.put(groupName, params);
	    	}
	    	params.put(paramName, paramValue);
 
	    }
	    
		settingService.save( settings );
		this.showSuccessJson("配置修改成功");
		return this.JSON_MESSAGE;
	}
	
	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}


	public String[] getCfg_values() {
		return cfg_values;
	}



	public void setCfg_values(String[] cfg_values) {
		this.cfg_values = cfg_values;
	}



	public String[] getCodes() {
		return codes;
	}



	public void setCodes(String[] codes) {
		this.codes = codes;
	}



	public void setTabs(Map tabs) {
		this.tabs = tabs;
	}

 

	public String getShowtab() {
		return showtab;
	}

	public void setShowtab(String showtab) {
		this.showtab = showtab;
	}

	public SettingPluginBundle getSettingPluginBundle() {
		return settingPluginBundle;
	}

	public void setSettingPluginBundle(SettingPluginBundle settingPluginBundle) {
		this.settingPluginBundle = settingPluginBundle;
	}

	public Map<Integer, String> getHtmls() {
		return htmls;
	}

	public void setHtmls(Map<Integer, String> htmls) {
		this.htmls = htmls;
	}
}
