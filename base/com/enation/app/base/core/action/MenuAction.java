package com.enation.app.base.core.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 菜单管理
 * @author kingapex
 * 2010-8-20下午12:36:03
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("menu")
@Results({
	@Result(name="tree", type="freemarker", location="/core/admin/menu/tree.html"),
	@Result(name="icon_list", type="freemarker", location="/core/admin/menu/icon_list.html"), 
	@Result(name="list", type="freemarker", location="/core/admin/menu/list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/menu/add.html"),
	@Result(name="edit", type="freemarker", location="/core/admin/menu/edit.html") 
})
public class MenuAction extends WWAction {
	private IMenuManager menuManager; 
	private List<Menu> menuList;
	private Menu menu;
	private Integer parentid;
	private Integer id;
	private Integer[] menu_ids;
	private Integer[] menu_sorts;
	private List iconList;
	private int targetid;
	private String movetype;
	private int authid;
	private IAuthActionManager authActionManager;
	public String tree(){
		
		return "tree";
	}
	
	
	public String listIcon(){
		iconList = new ArrayList();
		
		String app_apth = StringUtil.getRootPath();

		String dirpath  = app_apth+"/images/menuicon";
		File dir  = new File(dirpath);
		File[] files = dir.listFiles();
		for(File iconFile :files){			
			
			iconList.add(iconFile.getName());
			
		}
		
		return "icon_list";
	}
	 
	public String move(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try{
			this.menuManager.move(id, targetid, movetype);
			this.showSuccessJson("移动成功");
		}catch(Throwable e){
			this.logger.error("move menu",e);
			this.showErrorJson("移动菜单出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	public String json(){
		StringBuffer data  = new StringBuffer();
		data.append("[");
		menuList  = this.menuManager.getMenuTree(0);
		int i=0;
		for(Menu menu:menuList){
			if(i!=0){			
				data.append(",");			
			}
			data.append(this.toJson(menu));
			i++;
		}
		data.append("]");
		this.json=data.toString();
		return this.JSON_MESSAGE;
	}
	
	
	private String toJson(Menu menu){
		StringBuffer data  = new StringBuffer();
		data.append("{\"menuid\":"+menu.getId()+", \"name\":\""+menu.getTitle()+"\",\"isParent\":"+ menu.getHasChildren() );	
		if(menu.getHasChildren()){ //如果有子，继续
			data.append(",\"children\":[");
			int i=0;
			List<Menu> menuList= menu.getChildren();
			for(Menu child:menuList){
				if(i!=0){			
					data.append(",");			
				}
				data.append(this.toJson(child));
				i++;
			}
			data.append("]");
		}
		data.append("} ");
		return data.toString();
	}
	
	public String list(){
		menuList  = this.menuManager.getMenuTree(0);
		return "list";
	}
	
	public String add(){
		menuList  = this.menuManager.getMenuTree(0);
		return "add";
	}
	
	public String saveAdd(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能添加这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try{
			this.id=this.menuManager.add(menu);
			//json=JsonMessageUtil.getNumberJson("menuid", id);
			this.showSuccessJson("添加成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	 
	public String edit(){
		menuList  = this.menuManager.getMenuTree(0);
		menu = this.menuManager.get(id);
		return "edit";
	}
 
	
	public String saveEdit(){
		try{
			this.menuManager.edit(menu);
			this.showSuccessJson("保存成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
 
	
	public String updateSort(){
		try{
			this.menuManager.updateSort(menu_ids, menu_sorts);
			this.json="{result:1}";
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.json="{result:0,message:'"+e.getMessage()+"'}";
		}
		return this.JSON_MESSAGE;
	}
	
	public String delete(){
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能删除这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try{
			this.menuManager.delete(id);
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("删除失败");
		}		
		return this.JSON_MESSAGE;
	}
	public String getMenuJson(){
		StringBuffer data  = new StringBuffer();
		data.append("[");
		menuList  = this.menuManager.getMenuTree(0);
		com.enation.app.base.core.model.AuthAction authAction=null;
		if(authid!=0)
			authAction=  authActionManager.get(authid);
		int i=0;
		for(Menu menu:menuList){
			if(i!=0){			
				data.append(",");			
			}
				data.append(this.menutoJson(menu,authAction));
			i++;
		}
		data.append("]");
		this.json=data.toString();
		return this.JSON_MESSAGE;
	}
	private String menutoJson(Menu menu,com.enation.app.base.core.model.AuthAction authAction){
		StringBuffer data  = new StringBuffer();
		data.append("{\"id\":"+menu.getId()+", \"text\":\""+menu.getTitle()+"\"");	
			if(authAction!=null && authAction.getObjvalue()!=null){
			String[] menuids=authAction.getObjvalue().split(",");
			if(authAction!=null){
				for (int i = 0; i < menuids.length; i++) {
					if (NumberUtils.toInt(menuids[i]) == menu.getId()) {
						data.append(",\"checked\":true");
					}
				}
			}
		}
		if(menu.getHasChildren()){ //如果有子，继续
			data.append(",\"children\":[");
			int i=0;
			List<Menu> menuList= menu.getChildren();
			for(Menu child:menuList){
				if(i!=0){			
					data.append(",");			
				}
				if(authAction!=null){
					data.append(this.menutoJson(child,authAction));
				}else{
					data.append(this.menutoJson(child,null));
				}
				i++;
			}
			data.append("]");
		}
		data.append("} ");
		return data.toString();
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public List<Menu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Integer[] getMenu_ids() {
		return menu_ids;
	}

	public void setMenu_ids(Integer[] menuIds) {
		menu_ids = menuIds;
	}

	public Integer[] getMenu_sorts() {
		return menu_sorts;
	}

	public void setMenu_sorts(Integer[] menuSorts) {
		menu_sorts = menuSorts;
	}


	public List getIconList() {
		return iconList;
	}


	public void setIconList(List iconList) {
		this.iconList = iconList;
	}


	public int getTargetid() {
		return targetid;
	}


	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}


	public String getMovetype() {
		return movetype;
	}


	public void setMovetype(String movetype) {
		this.movetype = movetype;
	}


	public int getAuthid() {
		return authid;
	}


	public void setAuthid(int authid) {
		this.authid = authid;
	}


	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}


	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}

}
