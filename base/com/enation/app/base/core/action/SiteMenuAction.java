package com.enation.app.base.core.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.SiteMenu;
import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;


/**
 * 站点菜单
 * @author kingapex
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("siteMenu")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/sitemenu/menu_list.html"),
	@Result(name="input", type="freemarker", location="/core/admin/sitemenu/menu_input.html") 
})
public class SiteMenuAction extends WWAction {
	private ISiteMenuManager siteMenuManager ;
	private List menuList;
	private Integer[] sortArray;
	private Integer[] menuidArray;
	private Integer menuid;
	private SiteMenu siteMenu;
	private boolean isEdit;
	
	
	public String list(){
		return "list";
	}
	public String listJson(){
		menuList  = siteMenuManager.list(0);
		this.showGridJson(menuList);
		return this.JSON_MESSAGE;
	}
	
	public String updateSort(){
		try {
			siteMenuManager.updateSort(menuidArray, sortArray);
			this.showSuccessJson("保存排序成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("保存排序失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String add(){
		isEdit =false;
		this.menuList = this.siteMenuManager.list(0);
		siteMenu= new SiteMenu();
		return this.INPUT;
	}
	public String addchildren(){
		isEdit=false;
		this.menuList = this.siteMenuManager.list(0);
		menuid =siteMenuManager.get(menuid).getMenuid();
		siteMenu= new SiteMenu();
		return this.INPUT;
	}
	
	public String edit(){
		isEdit=true;
		this.menuList = this.siteMenuManager.list(0);
		siteMenu  =siteMenuManager.get(menuid);
		return this.INPUT;
	}
	
	public String save(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能添加这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		if(siteMenu.getUrl()!=null &&
                !siteMenu.getUrl().equals("")){
            if(siteMenu.getUrl().indexOf("http://www.gomecellar.com/")!=0){
                this.showErrorJson("导航链接请以http://www.gomecellar.com/开头！");
                return JSON_MESSAGE;
            }
        }
		if(menuid==null){
			this.siteMenuManager.add(siteMenu);
			this.showSuccessJson("菜单添加成功");
		}else{
			siteMenu.setMenuid(menuid);
			this.siteMenuManager.edit(siteMenu);
			this.showSuccessJson("菜单修改成功");
		}
		return this.JSON_MESSAGE;
	}
	
	public String delete(){
		if(EopSetting.IS_DEMO_SITE){
			if(menuid<=21){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		try {
			this.siteMenuManager.delete(menuid);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	public ISiteMenuManager getSiteMenuManager() {
		return siteMenuManager;
	}
	public void setSiteMenuManager(ISiteMenuManager siteMenuManager) {
		this.siteMenuManager = siteMenuManager;
	}
	public List getMenuList() {
		return menuList;
	}
	public void setMenuList(List menuList) {
		this.menuList = menuList;
	}

	public Integer[] getSortArray() {
		return sortArray;
	}

	public void setSortArray(Integer[] sortArray) {
		this.sortArray = sortArray;
	}

	public Integer[] getMenuidArray() {
		return menuidArray;
	}

	public void setMenuidArray(Integer[] menuidArray) {
		this.menuidArray = menuidArray;
	}

	public Integer getMenuid() {
		return menuid;
	}

	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}

	public SiteMenu getSiteMenu() {
		return siteMenu;
	}

	public void setSiteMenu(SiteMenu siteMenu) {
		this.siteMenu = siteMenu;
	}

	public boolean getIsEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
	
}
