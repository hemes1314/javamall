package com.enation.app.base.core.service.solution.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

public class SystemSettingInstaller implements IInstaller {
	
	private ISettingService settingService;
	private IDaoSupport daoSupport;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void install(String productId, Node fragment) {
		if(!"base".equals(productId)){
			return ;
		}
		 daoSupport.execute("insert into es_settings (cfg_group) values ( '"+SystemSetting.setting_key+"')");
		Map settings = settingService.getSetting();
		
		String app_apth = StringUtil.getRootPath();
		String app_domain= RequestUtil.getDomain();
		
		String static_server_domain = app_domain+"/statics";
		String static_server_path =  app_apth+"/statics";
		int backend_pagesize = 10;
		 HttpServletRequest request= ThreadContextHolder.getHttpRequest();
		 String default_img_url = static_server_domain+"/images/no_picture.jpg";
		 String context_path =request.getContextPath();
		 
		 Map systemSetting = new HashMap();
		 systemSetting.put("static_server_domain", static_server_domain);
		 systemSetting.put("static_server_path", static_server_path);
		 systemSetting.put("backend_pagesize", ""+backend_pagesize);
		 systemSetting.put("default_img_url", default_img_url);
		 systemSetting.put("context_path", context_path);
		 systemSetting.put("sms_reg_open", "0");
		 systemSetting.put("static_page_open", "0");
		 systemSetting.put("lucene", "0");
		 
		 systemSetting.put("wap_open", "0");
		 systemSetting.put("wap_folder", "");
		 systemSetting.put("wap_domain", "");
		 
		 String random = StringUtil.getRandStr(6); //生成一个6位的随机数做为密钥
		 random=StringUtil.md5(random);
		 systemSetting.put("global_auth_key", random); 
		 
		 settings.put(SystemSetting.setting_key, systemSetting);		 
		 settingService.save(settings); //保存系统设置
		 
		 
	}

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	

}
