package com.enation.eop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.base.core.service.ISettingService;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 系统设置
 * @author kingapex
 *
 */
public class SystemSetting {
	
	
	private static String static_server_domain; //静态服务器域名 
	private static String static_server_path;	//静态服务器路径
	private static int backend_pagesize; //后台分页数
	private static String  default_img_url; //默认图片路径
	private static String context_path="/"; //虚拟目录 
	private static String global_auth_key="123456";//加密密钥
	private static int static_page_open=0; //是否开启静态页生成
	private static int lucene=0; //是开启lucene索引
	private static int sms_reg_open=0; //是否开启短信注册
	private static int wap_open=0; //是否开启wap站点
	private static String wap_folder; //wap模板目录 
	private static String wap_domain;//wap站点域名
	
	
	public static final String setting_key="system"; //系统设置中的分组
	
	
	
	//系统设置的默认初始化
	static{
		String app_apth = StringUtil.getRootPath();
		String app_domain= RequestUtil.getDomain();
		
		 static_server_domain = app_domain+"/statics";
		 static_server_path =  app_apth+"/statics";
		 backend_pagesize = 10;
		 HttpServletRequest request= ThreadContextHolder.getHttpRequest();
		 default_img_url = static_server_domain+"/images/no_picture.jpg";
		 if(request!=null)
			  context_path =request.getContextPath();
		 
		 wap_folder="wap";
		 wap_domain=app_domain.replaceAll("www", "m");
		 
	}
	
	
	/**
	 * 加载系统设置
	 * 由数据库中加载
	 */
	public static void load(){
		
		ISettingService settingService= SpringContextHolder.getBean("settingService");
		Map<String,String> settings = settingService.getSetting(setting_key);
		
		static_server_domain = settings.get("static_server_domain");
		static_server_path = settings.get("static_server_path");
		
		String backend_pagesize_str = settings.get("backend_pagesize");
		backend_pagesize= StringUtil.toInt(backend_pagesize_str,10);
		
		default_img_url = settings.get("default_img_url");
		context_path = settings.get("context_path");
		global_auth_key =settings.get("global_auth_key");
		
		String static_page_open_str = settings.get("static_page_open");
		static_page_open= StringUtil.toInt(static_page_open_str,0);
		
		String  lucene_str = settings.get("lucene");
		lucene= StringUtil.toInt(lucene_str,0);
		
		String  sms_reg_open_str = settings.get("sms_reg_open");
		sms_reg_open= StringUtil.toInt(sms_reg_open_str,0);
		
		String  wap_open_str = settings.get("wap_open");
		wap_open= StringUtil.toInt(wap_open_str,0);
		
		
		wap_folder =settings.get("wap_folder");
		if( StringUtil.isEmpty(wap_folder ) ){
			wap_folder="wap";
		}
		
		
		wap_domain =settings.get("wap_domain");
		if( StringUtil.isEmpty(wap_domain ) ){
			wap_domain="";
		}
	}
	 
	
	public static int getBackend_pagesize() {
		 
		return backend_pagesize;
	}
	 
	public static String getStatic_server_domain() {
		return static_server_domain;
	}
	 
	public static String getStatic_server_path() {
		return static_server_path;
	}


	public static String getDefault_img_url() {
		return default_img_url;
	}

	public static String getContext_path() {
		return context_path;
	}


	public static String getGlobal_auth_key() {
		return global_auth_key;
	}


	public static int getStatic_page_open() {
		return static_page_open;
	}


	public static int getLucene() {
		return lucene;
	}


	public static int getSms_reg_open() {
		return sms_reg_open;
	}


 
	public static int getWap_open() {
		return wap_open;
 
 
	}
 

	public static String getWap_folder() {
		return wap_folder;
	}


	public static String getWap_domain() {
		
		return wap_domain;
	}

 
 
	
	
 
}
