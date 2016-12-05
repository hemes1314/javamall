package com.enation.app.base.core.action.api;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.action.WWAction;
/**
 * 地区api
 * @author lina
 * 2014-2-21
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/base")
@Action("region")
public class RegionApiAction extends WWAction {
	private IRegionsManager regionsManager;
	private Integer regionid;
	/**
	 * 获取该地区的子
	 * @param regionid int型
	 * @return
	 */
	public String getChildren(){
		if(regionid==null){
			this.showErrorJson("缺少参数：regionid");
		}else{
			List list =regionsManager.listChildrenByid(regionid);
			this.json=JSONArray.fromObject(list).toString();
		}
		return this.JSON_MESSAGE;
	}
	
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
	public Integer getRegionid() {
		return regionid;
	}
	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}


}
