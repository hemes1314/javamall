package com.enation.app.shop.component.orderreturns;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.ISqlFileExecutor;

/**
 * 订单退换货组件
 * @author kingapex
 *2012-2-10下午3:02:09
 */
@Component
public class OrderReturnsComponent implements IComponent {
	private IMenuManager menuManager;
	private IAuthActionManager authActionManager;
	@Override
	public void install() {
		

		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		int orderAuthId= PermissionConfig.getAuthId("order"); //订单管理员权限id
		
//		Menu parentMenu =menuManager.get("订单");		
//		
//		Menu menu = new Menu();
//		menu.setTitle("退换申请");
//		menu.setPid(parentMenu.getId());
//		menu.setUrl("#");
//		menu.setSorder(55);
//		menu.setMenutype(Menu.MENU_TYPE_APP );
//		int pmenuid = this.menuManager.add(menu);
//		
//		
//		Menu cmenu = new Menu();
//		cmenu.setTitle("退货申请");
//		cmenu.setPid(pmenuid);
//		cmenu.setUrl("/shop/admin/returnOrder!returnsList.do");
//		cmenu.setSorder(55);
//		cmenu.setMenutype(Menu.MENU_TYPE_APP );
//		int menuid = this.menuManager.add(cmenu);
		
//		this.authActionManager.addMenu(superAdminAuthId, new Integer[]{pmenuid,menuid});
//		this.authActionManager.addMenu(orderAuthId, new Integer[]{pmenuid,menuid});
		
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/orderreturns/orderreturns_install.xml","es_");

	}

	@Override
	public void unInstall() {
		
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		int orderAuthId= PermissionConfig.getAuthId("order"); //订单管理员权限id
		
		int menuid = menuManager.get("退货申请").getId();
		this.authActionManager.deleteMenu(superAdminAuthId, new Integer[]{menuid});
		this.authActionManager.deleteMenu(orderAuthId, new Integer[]{menuid});
		
		this.menuManager.delete("退货申请");
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/orderreturns/orderreturns_uninstall.xml","es_");

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
