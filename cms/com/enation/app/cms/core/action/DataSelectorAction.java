package com.enation.app.cms.core.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 数据选择器
 * @author kingapex
 *
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/cms")
@Action("article")
public class DataSelectorAction extends WWAction {
	
	private IDataManager dataManager;
	private IDataCatManager dataCatManager;
	private List<DataCat> catList;
	private List<DataField> fieldList;
	private IDataFieldManager dataFieldManager;
	private Integer catid;
	private Integer id;
	
	
	public String showDialog(){
		catList =dataCatManager.listAllChildren(0);
		return "dialog";
	}

//	public String list(){
//		this.fieldList = dataFieldManager.listByCatId(catid);
//		this.webpage =dataManager.list(catid, this.getPage(), 15);
//		return "list";
//	}
	public String list(){
		this.fieldList = dataFieldManager.listByCatId(catid);
		this.webpage =dataManager.list(catid, this.getPage(), 15);
		List list = (List) webpage.getResult();
		this.json = JsonMessageUtil.getListJson(list);
		return WWAction.JSON_MESSAGE;
	//	return "list";
	}
	public String detail(){
		this.fieldList = dataFieldManager.listByCatId(catid);
		try {
			Map  list = dataManager.get(id, catid, false);
			this.json = JsonMessageUtil.getListJson(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return WWAction.JSON_MESSAGE;
		
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public IDataCatManager getDataCatManager() {
		return dataCatManager;
	}

	public void setDataCatManager(IDataCatManager dataCatManager) {
		this.dataCatManager = dataCatManager;
	}

	public List<DataCat> getCatList() {
		return catList;
	}

	public void setCatList(List<DataCat> catList) {
		this.catList = catList;
	}

	public IDataFieldManager getDataFieldManager() {
		return dataFieldManager;
	}

	public void setDataFieldManager(IDataFieldManager dataFieldManager) {
		this.dataFieldManager = dataFieldManager;
	}

	public List<DataField> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<DataField> fieldList) {
		this.fieldList = fieldList;
	}

	
}
