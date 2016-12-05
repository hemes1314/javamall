package com.enation.app.base.core.action;

import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IAccessRecorder;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.action.WWAction;

/**
 * 首页项(基本)
 * @author kingapex
 * 2010-10-13下午05:16:45
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("indexItem")
@Results({
	@Result(name="base", type="freemarker", location="/core/admin/index/base.html"),
	@Result(name="access", type="freemarker", location="/core/admin/index/access.html"),
	@Result(name="point", type="freemarker", location="/core/admin/index/point.html")
})
public class BaseIndexItemAction extends WWAction {
	private ISiteManager siteManager;
	private IAccessRecorder accessRecorder; 
	private Map accessMap; 
	private EopSite site;
	private int canget;
	
	public String base() {
		site =EopSite.getInstance();
		return "base";
	}

	public String access() {
		this.accessMap = this.accessRecorder.census();
		return "access";
	}
	
 
	
	public ISiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(ISiteManager siteManager) {
		this.siteManager = siteManager;
	}

	public EopSite getSite() {
		return site;
	}

	public void setSite(EopSite site) {
		this.site = site;
	}

	public IAccessRecorder getAccessRecorder() {
		return accessRecorder;
	}

	public void setAccessRecorder(IAccessRecorder accessRecorder) {
		this.accessRecorder = accessRecorder;
	}

	public Map getAccessMap() {
		return accessMap;
	}

	public void setAccessMap(Map accessMap) {
		this.accessMap = accessMap;
	}

	public int getCanget() {
		return canget;
	}

	public void setCanget(int canget) {
		this.canget = canget;
	}
	
}
