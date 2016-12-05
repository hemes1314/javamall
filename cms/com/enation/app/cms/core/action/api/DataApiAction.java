package com.enation.app.cms.core.action.api;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.action.WWAction;

/**
 * 文章api
 * @author liuzy
 *
 */

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/cms")
@Action("data")

public class DataApiAction extends WWAction {
	private IDataManager dataManager;
	private IDataFieldManager dataFieldManager;
	
	private Integer modelid;
	
	public String search() {
		this.json = JSONArray.fromObject(dataManager.search(modelid, null)).toString();
		return WWAction.JSON_MESSAGE;
	}

	public String fields() {
		this.json = JSONArray.fromObject(dataFieldManager.list(modelid)).toString();
		return WWAction.JSON_MESSAGE;
	}
	
	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public Integer getModelid() {
		return modelid;
	}

	public void setModelid(Integer modelid) {
		this.modelid = modelid;
	}

	public IDataFieldManager getDataFieldManager() {
		return dataFieldManager;
	}

	public void setDataFieldManager(IDataFieldManager dataFieldManager) {
		this.dataFieldManager = dataFieldManager;
	}
	
}
