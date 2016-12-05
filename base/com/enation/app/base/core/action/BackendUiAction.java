package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 后台界面Action
 * @author kingapex
 *
 */
@SuppressWarnings("serial")
public class BackendUiAction extends WWAction {
	
	private IAdminThemeManager adminThemeManager;
	private IAdminUserManager adminUserManager;
	private IMenuManager menuManager ;
	private IPermissionManager permissionManager ;
	private IAuthActionManager authActionManager;
	
	private String theme;
	private EopSite site;
	private String username; 
	private String version;
	private AdminUser user;
	private String timeout;
	private String referer;
	private int type;
	private List menuList;
	private String ctx;
	private String product_type;
	
	public String login(){
		putCommonData();
		return "login_page";
	}
	
	public String main(){
		user = UserConext.getCurrentAdminUser();
		putCommonData();
		version = EopSetting.VERSION;
		product_type = EopSetting.PRODUCT;
		
		 
		if (user.getFounder() != 1) {
			this.menuList=this.menuManager.newMenutree(0, user);
		}else{
			this.menuList = this.menuManager.getMenuTree(0);
		}
		//this.menuList = this.menuManager.getMenuTree(0);
		this.ctx = this.getRequest().getContextPath();
		if("/".equals(ctx)){
			ctx="";
		}

		return "main_page";
	}
	
	private void putCommonData(){
		site = EopSite.getInstance().getInstance();
 
//		site.geta
		
		// 读取后台使用的模板
		AdminTheme theTheme = adminThemeManager.get(site.getAdminthemeid());
		theme = "default";
		if (theTheme != null) {
			theme = theTheme.getPath();
		}
		this.ctx = this.getRequest().getContextPath();
		if("/".equals(ctx)){
			ctx="";
		}
	}
	
	
	public String menuJson(){
		this.json = this.getMenuJson();
		return this.JSON_MESSAGE;
	}

	public String getMenuJson() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String showall = request.getParameter("showall");
		StringBuffer json = new StringBuffer();

		/*
		 * 调用核心api读取站点的菜单
		 */

		
		List<Menu> tempMenuList = menuManager.getMenuList();
		List<Menu> menuList = new ArrayList<Menu>();
	
		AdminUser user = UserConext.getCurrentAdminUser();
		user = adminUserManager.get(user.getUserid());
		List<AuthAction> authList = permissionManager.getUesrAct(user.getUserid(), "menu");

		for (Menu menu : tempMenuList) {
			if (menu.getMenutype().intValue() == Menu.MENU_TYPE_APP) {
				if (!"yes".equals(showall)) {
					if (user.getFounder() != 1) {
						if (!checkPermssion(menu, authList)) {
							continue;
						}
					} else {
						int superAdminAuthId = PermissionConfig.getAuthId("super_admin"); // 超级管理员权限id
						AuthAction superAuth = authActionManager.get(superAdminAuthId);
						if (superAuth != null && !checkPermssion(menu, superAuth)) {
							continue;
						}
					}
				}
			}
			menuList.add(menu);
		}
		List<Menu> syslist = getMenuList(Menu.MENU_TYPE_SYS, menuList);
		List<Menu> applist = getMenuList(Menu.MENU_TYPE_APP, menuList);
		List<Menu> extlist = getMenuList(Menu.MENU_TYPE_EXT, menuList);

		json.append("var menu ={");
		json.append("'sys':[");
		json.append(toJson(syslist, menuList));
		json.append("]");

		json.append(",'app':[");
		json.append(toJson(applist, menuList));
		json.append("]");

		json.append(",'ext':[");
		json.append(toJson(extlist, menuList));
		json.append("]");
		json.append("};");

		json.append("var mainpage=true;");
		json.append("var domain='" + request.getServerName() + "';");
		json.append("var app_path='" + request.getContextPath() + "';");

		return json.toString();
	}

	/**
	 * 将一个menuList转为json数组
	 * 
	 * @param menuList
	 * @return
	 */
	public String toJson(List<Menu> menuList, List<Menu> allList) {
		StringBuffer menuItem = new StringBuffer();
		int i = 0;
		for (Menu menu : menuList) {
			if (i != 0)
				menuItem.append(",");
			menuItem.append(toJson(menu, allList));
			i++;
		}
		return menuItem.toString();
	}

	private boolean checkPermssion(Menu menu, List<AuthAction> authList) {
		for (AuthAction auth : authList) {
			if (checkPermssion(menu, auth)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkPermssion(Menu menu, AuthAction auth) {
		String values = auth.getObjvalue();
		if (values != null) {
			String[] value_ar = StringUtils.split(values, ",");// values.split(",");
			for (String v : value_ar) {
				if (v.equals("" + menu.getId().intValue())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据menu实体生成json字串
	 * 
	 * @param menu
	 * @return
	 */
	private String toJson(Menu menu, List<Menu> menuList) {
		String defaulticon = "default.png";
		
		String title = menu.getTitle();
		String url = menu.getUrl();
		Integer selected = menu.getSelected();
		String type = menu.getDatatype();
		String target = menu.getTarget();
		String icon = menu.getIcon();
		String iconhover = menu.getIcon_hover();
		
		if(StringUtils.isEmpty(icon)){
			icon =defaulticon;
		}
		
		
		if (!"_blank".equals(target)) {
			HttpServletRequest httpRequest= ThreadContextHolder.getHttpRequest();
			String ctx = httpRequest.getContextPath();
			ctx = ctx.equals("/") ? "" : ctx;
			url = ctx + url;
		}
		StringBuffer menuItem = new StringBuffer();

		menuItem.append("{");
		menuItem.append("\"id\":");
		menuItem.append(menu.getId());

		menuItem.append(",\"text\":\"");
		menuItem.append(title);
		menuItem.append("\"");

		menuItem.append(",\"url\":\"");
		menuItem.append(url);
		menuItem.append("\"");

		menuItem.append(",\"default\":");
		menuItem.append(selected);

		menuItem.append(",\"children\":");
		menuItem.append(getChildrenJson(menu.getId(), menuList));

		menuItem.append(",\"type\":\"");
		menuItem.append(type);
		menuItem.append("\"");

		menuItem.append(",\"target\":\"");
		menuItem.append(target);
		menuItem.append("\"");

		menuItem.append(",\"icon\":\"");
		menuItem.append(icon);
		menuItem.append("\"");
		
		menuItem.append(",\"icon_hover\":\"");
		menuItem.append(iconhover);
		menuItem.append("\"");
		
		menuItem.append("}");

		return menuItem.toString();
	}

	/**
	 * 根据菜单某种类型过滤出menuList
	 * 
	 * @param menuType
	 * @param menuList
	 * @return
	 */
	public List<Menu> getMenuList(int menuType, List<Menu> menuList) {
		List<Menu> mlist = new ArrayList<Menu>();

		for (Menu menu : menuList) {
			if (menu.getMenutype().intValue() == menuType
					&& menu.getPid().intValue() == 0) {
				mlist.add(menu);
			}
		}
		return mlist;
	}

	/**
	 * 读取
	 * 
	 * @param menuId
	 * @param menuList
	 * @return
	 */
	private String getChildrenJson(Integer menuId, List<Menu> menuList) {
		StringBuffer json = new StringBuffer();
		json.append("[");
		int i = 0;
		for (Menu menu : menuList) {
			if (menuId.intValue() == menu.getPid().intValue()) {
				if (i != 0)
					json.append(",");
				json.append(toJson(menu, menuList));
				i++;
			}
		}
		json.append("]");
		return json.toString();
	}

	
	
	
	public IAdminThemeManager getAdminThemeManager() {
		return adminThemeManager;
	}
	public void setAdminThemeManager(IAdminThemeManager adminThemeManager) {
		this.adminThemeManager = adminThemeManager;
	}
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}



	public EopSite getSite() {
		return site;
	}



	public void setSite(EopSite site) {
		this.site = site;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getVersion() {
		return version;
	}



	public void setVersion(String version) {
		this.version = version;
	}

	public AdminUser getUser() {
		return user;
	}

	public void setUser(AdminUser user) {
		this.user = user;
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}

	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List getMenuList() {
		return menuList;
	}

	public void setMenuList(List menuList) {
		this.menuList = menuList;
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}
	
	
}
