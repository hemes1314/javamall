package com.enation.app.shop.component.giftcard;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.IDaoSupport;

@Component
public class GiftcardComponent implements IComponent {
    
    private IMenuManager menuManager;
    private IAuthActionManager authActionManager;
    private IPermissionManager permissionManager;
    private IDaoSupport daoSupport;
    
	@Override
	public void install() {
	    DBSolutionFactory.dbImport("file:com/enation/app/shop/component/giftcard/giftcard_install.xml", "es_");
		
	  //判断是否为超级管理员
        boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));//超级管理员权限
        if(!isSuperAdmin)
            return;
        
        Menu parentMenu = menuManager.get("促销活动管理");
        if (parentMenu == null) {
            Menu mainMenu = menuManager.get("促销");
            parentMenu = new Menu();
            parentMenu.setTitle("促销活动管理");
            parentMenu.setPid(mainMenu.getId());
            parentMenu.setUrl("");
            parentMenu.setSorder(1);
            parentMenu.setMenutype(Menu.MENU_TYPE_APP);
            this.menuManager.add(parentMenu);
            parentMenu.setId(this.daoSupport.getLastId("es_menu"));
        }
        Menu menu = new Menu();
        menu.setTitle("礼品卡管理");
        menu.setPid(parentMenu.getId());
        menu.setUrl("/shop/admin/giftcard-type!list.do");
        menu.setSorder(70);
        menu.setMenutype(Menu.MENU_TYPE_APP);
        this.menuManager.add(menu);
	}

	@Override
	public void unInstall() {
	  //判断是否为超级管理员
        boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));//超级管理员权限
        if(!isSuperAdmin)
            return;
        int superAdminAuthId = PermissionConfig.getAuthId("super_admin"); // 超级管理员权限id
        //删除主目录：团购管理
        Menu menu =  menuManager.get("礼品卡管理");
        if(menu!=null){
            int addmenuid =menu.getId();
            this.authActionManager.deleteMenu(superAdminAuthId, new Integer[] {addmenuid });
            this.menuManager.delete("礼品卡管理");
        }
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
    
    public IPermissionManager getPermissionManager() {
        return permissionManager;
    }
    public void setPermissionManager(IPermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}
