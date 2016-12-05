package com.enation.app.base.core.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.resource.IIndexItemManager;
import com.enation.eop.resource.model.IndexItem;
import com.enation.framework.action.WWAction;

/**
 * 后台首页
 * 
 * @author kingapex 2010-10-12下午04:53:52
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("index")
@Results({
	@Result(name="index", type="freemarker", location="/core/admin/index.html")
})
public class IndexAction extends WWAction {

	private IIndexItemManager indexItemManager;
	private List<IndexItem> itemList;

	public String execute() {
		itemList = indexItemManager.list();
		return "index";
	}

	public IIndexItemManager getIndexItemManager() {
		return indexItemManager;
	}

	public void setIndexItemManager(IIndexItemManager indexItemManager) {
		this.indexItemManager = indexItemManager;
	}

	public List<IndexItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<IndexItem> itemList) {
		this.itemList = itemList;
	}

}
