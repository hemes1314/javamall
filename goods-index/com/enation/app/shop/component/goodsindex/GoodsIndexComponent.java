/**
 * 
 */
package com.enation.app.shop.component.goodsindex;

import java.io.File;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.component.IComponent;
import com.enation.framework.component.IComponentStartAble;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

/**
 * 商品全文索引组件
 * @author kingapex
 *2015-4-16
 */
@Component
public class GoodsIndexComponent implements IComponent,IComponentStartAble {
	private ISettingService settingService;
	private IDaoSupport daoSupport;
	private IMenuManager menuManager;
	private IAuthActionManager authActionManager;

	/**
	 * 安装lucene组件时写入lucene的设置
	 */
	@Override
	public void install() {
		
		 daoSupport.execute("insert into es_settings (cfg_group) values ( '"+LuceneSetting.SETTING_KEY+"')");
		 
		 String index_path  = StringUtil.getRootPath()+"/index_path";
		 
		 settingService.save(LuceneSetting.SETTING_KEY, "index_path", index_path);
		 
		 LuceneSetting.INDEX_PATH= index_path;
		 
		 String facet_path = LuceneSetting.INDEX_PATH+"/facet";
		 String con_path = LuceneSetting.INDEX_PATH+"/content";
		 
		 
		 //先全部删除，再重建
		 File file= new File(con_path);
		 file.deleteOnExit();
		 file.mkdirs();
		 
		 file= new File(facet_path);
		 file.deleteOnExit();
		 file.mkdirs();
		 
		 this.addMenu();
		 
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/goodsindex/lucene-install.xml", "es_");

	}
	
	private static final String parentMenuName="网店设置";
	
	/**
	 * 为组件添加菜单
	 */
	private void addMenu(){
		int superAdminAuthId = PermissionConfig.getAuthId("super_admin"); // 超级管理员权限id

		Menu parentMenu = menuManager.get(parentMenuName);
		Menu menu = new Menu();
		menu.setTitle("生成索引");
		menu.setPid(parentMenu.getId());
		menu.setUrl("/shop/admin/goods-index.do");
		menu.setSorder(105);
		menu.setMenutype(Menu.MENU_TYPE_APP);
		this.menuManager.add(menu);
		
	}

	
	
	/**
	 * 删除组件相应的菜单
	 */
	private void deleteMenu(){
		int superAdminAuthId = PermissionConfig.getAuthId("super_admin"); // 超级管理员权限id
		Menu menu =  menuManager.get("生成索引");
		if(menu!=null){
			int addmenuid =menu.getId();
			this.authActionManager.deleteMenu(superAdminAuthId, new Integer[] {addmenuid });
			this.menuManager.delete("生成索引");
		}
	}
	
	
	/**
	 * 卸载lucene组件时删除lucene的设置
	 */
	@Override
	public void unInstall() {
		settingService.delete("'"+LuceneSetting.SETTING_KEY+"'" );
		deleteMenu();
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/goodsindex/lucene-uninstall.xml", "es_");

	}

	
	/**
	 * 在组件启动时加载lucene配置
	 */
	@Override
	public void start() {
		String index_path  = settingService.getSetting(LuceneSetting.SETTING_KEY, "index_path");
		LuceneSetting.INDEX_PATH= index_path;
	}
	
	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}



	public IMenuManager getMenuManager() {
		return menuManager;
	}



	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}



	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}



	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}



	

	
	
}
