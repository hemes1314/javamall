package com.enation.app.b2b2c.core.tag.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreDlyType;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreRegionsManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 店铺模板标签
 * 
 * @author xulipeng
 *
 */
@Component
public class StoreTransportListTag extends BaseFreeMarkerTag {

	private IStoreDlyTypeManager storeDlyTypeManager;
	private IStoreTemplateManager storeTemplateManager;
	private IStoreRegionsManager storeRegionsManager;
	private IStoreMemberManager storeMemberManager;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember storeMember = storeMemberManager.getStoreMember();
		
		//取出属于这个店铺的所有模板
		List<Map> templateList = this.storeTemplateManager.getTemplateList(storeMember.getStore_id());
		
		for(Map map:templateList){
			Integer template_id = (Integer) map.get("id");
			List<Map> dlytypelist = this.storeDlyTypeManager.getDlyTypeList(template_id);
			map.put("dlylist", dlytypelist);
			
			//根据配送方式取出所有的指定地区配送
			for(Map dlymap :dlytypelist){
				Integer type_id = (Integer) dlymap.get("type_id");
				List<Map> arealist = this.storeDlyTypeManager.getDlyTypeAreaList(type_id);
				dlymap.put("arealist", arealist);
				dlymap.put("area", "全国");
				DlyTypeConfig dlyConfig = convertTypeJson((String) dlymap.get("config"));
				dlymap.put("dlyConfig", dlyConfig);
				
				//查询并设置指定地区的运送到地区
				for(Map areamap:arealist){
					areamap.put("area", areamap.get("area_name_group"));
					DlyTypeConfig areaConfig = convertTypeJson((String) areamap.get("config"));
					areamap.put("areaConfig", areaConfig);
				}
			}
		}
		
		return templateList;
	}
	
	private DlyTypeConfig convertTypeJson(String config) {
		
		JSONObject typeJsonObject = JSONObject.fromObject(config);
		DlyTypeConfig typeConfig = (DlyTypeConfig) JSONObject.toBean(
				typeJsonObject, DlyTypeConfig.class);
		
		return typeConfig;
		
	}
	
	//set get
	
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
