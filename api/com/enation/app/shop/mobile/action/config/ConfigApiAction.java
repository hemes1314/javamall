/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：系统配置api
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.SystemSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonUtil;

/**
 * 系统配置Api 
 * 提供系统参数js】
 * @author Sylow
 * @version v1.0 2015-09-08
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("config")
public class ConfigApiAction extends WWAction {
	
	
	private String  default_img_url; //默认图片路径
	
	/**
	 * 获得一些系统配置
	 * @return
	 */
	public String get(){
		
		
		// 默认图片url
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("static_server_domain", SystemSetting.getStatic_server_domain());
		map.put("default_img_url", SystemSetting.getDefault_img_url());
		
		this.json = "var APP_SETTING = " + JsonUtil.MapToJson(map);
		return WWAction.JSON_MESSAGE;
	}
	
	public String getDefault_img_url() {
		return default_img_url;
	}

	public void setDefault_img_url(String default_img_url) {
		this.default_img_url = default_img_url;
	}

}
