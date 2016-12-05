package com.enation.app.base.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.enation.app.base.core.model.SmsPlatform;
import com.enation.app.base.core.plugin.sms.ISmsSendEvent;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.plugin.IPlugin;

@SuppressWarnings("rawtypes")
public class SmsManager extends BaseSupport implements ISmsManager {

	@Override
	public List getList() {
		List list = this.daoSupport.queryForList("select * from es_sms_platform");
		return list;
	}

	@Override
	public void addSmsPlatform(SmsPlatform smsPlatform) {
		this.daoSupport.insert("es_sms_platform", smsPlatform);
	}

	@Override
	public String getSmsPlatformHtml(String pluginid,Integer smsid) {
		
		FreeMarkerPaser fp =  FreeMarkerPaser.getInstance();
		IPlugin installPlugin = null;
		installPlugin = SpringContextHolder.getBean(pluginid);
		fp.setClz(installPlugin.getClass());
		
		Map<String,String> params = this.getConfigParams(smsid);
 
		fp.putData(params);
		
		return fp.proessPageContent();
	}
	
	public Map<String, String> getConfigParams(Integer id) {
		SmsPlatform platform = this.get(id);
		String config  = platform.getConfig();
		if(null == config ) return new HashMap<String,String>();
		JSONObject jsonObject = JSONObject.fromObject( config );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		return itemMap;
	}

	@Override
	public void setParam(Integer id, Map<String,String> param) {
		String sql ="update es_sms_platform set config=? where id=?";
		this.daoSupport.execute(sql, JSONObject.fromObject(param).toString(),id);
	}

	@Override
	public SmsPlatform get(Integer id) {
		String sql = "select * from es_sms_platform where id=?";
		SmsPlatform platform =  (SmsPlatform) this.daoSupport.queryForObject(sql, SmsPlatform.class, id);
		return platform;
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean send(String phone, String content) {
		boolean flag = false;
		try {
			String sql ="select * from es_sms_platform where is_open=1";
			SmsPlatform platform =  (SmsPlatform) this.daoSupport.queryForObject(sql, SmsPlatform.class);
			String config = platform.getConfig();
			JSONObject jsonObject = JSONObject.fromObject( config );  
			Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
			ISmsSendEvent smsSendEvent = SpringContextHolder.getBean(platform.getCode());
			flag = smsSendEvent.onSend(phone, content, itemMap);
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
		return flag;
	}
	
	@Override
	public void open(Integer id) {
		this.daoSupport.execute("update es_sms_platform set is_open=0");
		this.daoSupport.execute("update es_sms_platform set is_open=1 where id=?", id);
	}

}
