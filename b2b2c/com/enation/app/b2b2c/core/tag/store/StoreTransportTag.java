package com.enation.app.b2b2c.core.tag.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreRegionsManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


@Component
public class StoreTransportTag extends BaseFreeMarkerTag {

	private IStoreDlyTypeManager storeDlyTypeManager;
	private IStoreTemplateManager storeTemplateManager;
	private IStoreRegionsManager storeRegionsManager;
	private IStoreMemberManager storeMemberManager;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer tempid = (Integer) params.get("tempid");
		StoreMember storeMember = storeMemberManager.getStoreMember();
		
		Map map = this.storeTemplateManager.getTemplae(storeMember.getStore_id(), tempid);
		
		Integer template_id = (Integer) map.get("id");
		List<Map> dlytypelist = this.storeDlyTypeManager.getDlyTypeList(template_id);
		map.put("dlylist", dlytypelist);

		//根据配送方式取出所有的指定地区配送
		for(Map dlymap :dlytypelist){
			Integer type_id = (Integer) dlymap.get("type_id");
			List<Map> arealist = this.storeDlyTypeManager.getDlyTypeAreaList(type_id);
			dlymap.put("arealist", arealist);
			DlyTypeConfig dlyConfig = convertTypeJson((String) dlymap.get("config"));
			dlymap.put("dlyConfig", dlyConfig);
			
			String name = (String) dlymap.get("name");
			if(name.equals("平邮")){
				dlymap.put("tpltype", 1);
				map.put("py", 1);
			}else if(name.equals("快递")){
				dlymap.put("tpltype", 2);
				map.put("kd", 1);
			}else if (name.equals("邮政")) {
				dlymap.put("tpltype", 3);
				map.put("yz", 1);
			}
			
			//查询并设置指定地区的运送到地区
			for(Map areamap:arealist){
				areamap.put("area", areamap.get("area_name_group"));
				DlyTypeConfig areaConfig = convertTypeJson((String) areamap.get("config"));
				areamap.put("areaConfig", areaConfig);
			}
		}
		
		return map;
	}
	
	private DlyTypeConfig convertTypeJson(String config) {
		
		JSONObject typeJsonObject = JSONObject.fromObject(config);
		DlyTypeConfig typeConfig = (DlyTypeConfig) JSONObject.toBean(
				typeJsonObject, DlyTypeConfig.class);
		
		return typeConfig;
		
	}
	
	//set  get
 
	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public IStoreRegionsManager getStoreRegionsManager() {
		return storeRegionsManager;
	}

	public void setStoreRegionsManager(IStoreRegionsManager storeRegionsManager) {
		this.storeRegionsManager = storeRegionsManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	
}
