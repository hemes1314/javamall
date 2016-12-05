package com.enation.app.base.core.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.SmsPlatform;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.framework.action.WWAction;


/**
 * 短信Action
 * @author xulipeng
 *
 */

public class SmsAction extends WWAction {

	private ISmsManager smsManager;
	private String pluginid;
	private String param_html;
	private Integer smsid;
	private SmsPlatform platform;
	
	/**
	 * 跳转至短信平台列表页
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 跳转至设置参数页
	 * @return
	 */
	public String gethtml(){
		
		platform = this.smsManager.get(smsid);
		param_html = this.smsManager.getSmsPlatformHtml(pluginid,smsid);
		
		return "edit";
	}
	
	/**
	 * 查询短信平台列表json数据
	 * @return
	 */
	public String listJson(){
		List list = this.smsManager.getList();
		this.showGridJson(list);
		return JSON_MESSAGE;
	}
	
	/**
	 * 保存修改参数
	 * @return
	 */
	public String saveEdit(){
		try{
			HttpServletRequest  request = this.getRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String name= names.nextElement();
				
				if("smsid".equals(name)) continue;
				String value = request.getParameter(name);
				params.put(name, value);
			}
			this.smsManager.setParam(smsid, params);
			
			this.showSuccessJson("设置成功");
		}catch(Exception e){
			this.logger.error("设置短信网关canshu出错", e);
			this.showSuccessJson("设置失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 设置默认启用sms平台
	 * @return
	 */
	public String setOpen(){
		try {
			this.smsManager.open(smsid);
			this.showSuccessJson("已启用");
		} catch (Exception e) {
			this.showErrorJson("启用失败");
		}
		return JSON_MESSAGE;
	}
	
	
	//set  get 

	public ISmsManager getSmsManager() {
		return smsManager;
	}

	public void setSmsManager(ISmsManager smsManager) {
		this.smsManager = smsManager;
	}

	public String getPluginid() {
		return pluginid;
	}

	public void setPluginid(String pluginid) {
		this.pluginid = pluginid;
	}

	public String getParam_html() {
		return param_html;
	}

	public void setParam_html(String param_html) {
		this.param_html = param_html;
	}

	public Integer getSmsid() {
		return smsid;
	}

	public void setSmsid(Integer smsid) {
		this.smsid = smsid;
	}

	public SmsPlatform getPlatform() {
		return platform;
	}

	public void setPlatform(SmsPlatform platform) {
		this.platform = platform;
	}
	
	
}
