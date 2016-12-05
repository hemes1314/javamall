package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.SitemapPluginBundle;
import com.enation.app.base.core.service.ISitemapManager;
import com.enation.framework.action.WWAction;

/**
 * 后台重建站点地图
 * @author lzf<br/>
 * 2010-12-2 下午01:31:52<br/>
 * version 2.1.5
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("createSitemap")
@Results({
})
public class CreateSitemapAction extends WWAction {

	private ISitemapManager sitemapManager;
	private SitemapPluginBundle sitemapPluginBundle;
	
	public String recreate(){
		this.clean();
		this.sitemapPluginBundle.onRecreateMap();
		this.msgs.add("站点地图创建成功");
		this.urls.put("访问站点地图", "/sitemap.xml");
		return this.MESSAGE;
	}
	private void clean(){
		this.sitemapManager.clean();
	}
	
	public ISitemapManager getSitemapManager() {
		return sitemapManager;
	}
	public void setSitemapManager(ISitemapManager sitemapManager) {
		this.sitemapManager = sitemapManager;
	}
	public SitemapPluginBundle getSitemapPluginBundle() {
		return sitemapPluginBundle;
	}
	public void setSitemapPluginBundle(SitemapPluginBundle sitemapPluginBundle) {
		this.sitemapPluginBundle = sitemapPluginBundle;
	}
}