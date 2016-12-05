package com.enation.app.base.core.service.solution;

/**
 * 根据类型返回合适的Installer,此文件必须工作于spring 容器下
 * @author kingapex
 * 2010-1-20下午07:20:34
 */
public class InstallerFactory {
	public static final String TYPE_APP ="apps";
	public static final String TYPE_MENU ="menus";
	public static final String TYPE_ADMINTHEME ="adminThemes";
	public static final String TYPE_THEME ="themes";
	public static final String TYPE_URL ="urls";
	public static final String TYPE_WIDGET ="widgets";
	public static final String TYPE_INDEX_ITEM ="indexitems";
	public static final String TYPE_COMPONENT="components";
	public static final String TYPE_SITE="site";
	
	
	private IInstaller menuInstaller;
	private IInstaller adminThemeInstaller;
	private IInstaller themeInstaller;
	private IInstaller uriInstaller;

	private IInstaller appInstaller;
	private IInstaller indexItemInstaller;
	private IInstaller componentInstaller;
	private IInstaller siteInstaller;
	
	
	public IInstaller getInstaller(String type){
		
		
		if(TYPE_APP.equals(type)){
			return this.appInstaller;
		}
		
		if(TYPE_MENU.equals(type)){
			return this.menuInstaller;
		}
		

		if(TYPE_ADMINTHEME.equals(type)){
			return this.adminThemeInstaller;
		}
		
		if(TYPE_THEME.equals(type)){
			return this.themeInstaller;
			
		}

		if(TYPE_URL.equals(type)){
			return this.uriInstaller;
		}

	
		
		if(TYPE_INDEX_ITEM.equals(type)){
			return this.indexItemInstaller;
		}
		
		if(TYPE_COMPONENT.equals(type)){
			return this.componentInstaller;
		}
 
		if(TYPE_SITE.equals(type)){
			return this.siteInstaller;
		}
		throw new  RuntimeException(" get Installer instance error[incorrect type param]");
	}

	public void setMenuInstaller(IInstaller menuInstaller) {
		this.menuInstaller = menuInstaller;
	}

	public void setAdminThemeInstaller(IInstaller adminThemeInstaller) {
		this.adminThemeInstaller = adminThemeInstaller;
	}

	public void setThemeInstaller(IInstaller themeInstaller) {
		this.themeInstaller = themeInstaller;
	}

	public void setUriInstaller(IInstaller uriInstaller) {
		this.uriInstaller = uriInstaller;
	}



	public IInstaller getAppInstaller() {
		return appInstaller;
	}

	public void setAppInstaller(IInstaller appInstaller) {
		this.appInstaller = appInstaller;
	}

	public void setIndexItemInstaller(IInstaller indexItemInstaller) {
		this.indexItemInstaller = indexItemInstaller;
	}

	public void setComponentInstaller(IInstaller componentInstaller) {
		this.componentInstaller = componentInstaller;
	}

	public IInstaller getSiteInstaller() {
		return siteInstaller;
	}

	public void setSiteInstaller(IInstaller siteInstaller) {
		this.siteInstaller = siteInstaller;
	}
}
