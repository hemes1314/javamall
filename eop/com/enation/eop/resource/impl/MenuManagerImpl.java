package com.enation.eop.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;

/**
 * 菜单管理
 * 
 * @author kingapex 2010-5-10下午02:00:10
 */
public class MenuManagerImpl extends BaseSupport implements IMenuManager {
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean() {
		this.baseDaoSupport.execute("truncate table menu");
	}

	public List<Menu> getMenuList() {
		return this.baseDaoSupport.queryForList("select * from menu where deleteflag = '0' order by sorder asc", Menu.class);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(Menu menu) {
		if (menu.getTitle() == null)
			throw new IllegalArgumentException("title argument is null");
		if (menu.getPid() == null)
			throw new IllegalArgumentException("pid argument is null");
		if (menu.getUrl() == null)
			throw new IllegalArgumentException("url argument is null");
		if (menu.getSorder() == null)
			throw new IllegalArgumentException("sorder argument is null");
		menu.setDeleteflag(0);
		this.baseDaoSupport.insert("menu", menu);
		return this.baseDaoSupport.getLastId("menu");
	}

	public List<Menu> getMenuTree(Integer menuid) {
		if (menuid == null)
			throw new IllegalArgumentException("menuid argument is null");
		List<Menu> menuList = this.getMenuList();
		List<Menu> topMenuList = new ArrayList<Menu>();
		for (Menu menu : menuList) {
			if (menu.getPid().compareTo(menuid) == 0) {
				List<Menu> children = this.getChildren(menuList, menu.getId());
				menu.setChildren(children);
				menu.setState("closed");
				topMenuList.add(menu);
			}
		}
		return topMenuList;
	}

	/**
	 * 在一个集合中查找子
	 * 
	 * @param menuList
	 *            所有菜单集合
	 * @param parentid
	 *            父id
	 * @return 找到的子集合
	 */
	private List<Menu> getChildren(List<Menu> menuList, Integer parentid) {
		List<Menu> children = new ArrayList<Menu>();
		for (Menu menu : menuList) {
			if (menu.getPid().compareTo(parentid) == 0) {
				menu.setChildren(this.getChildren(menuList, menu.getId()));
				children.add(menu);
			}
		}
		return children;
	}

	public Menu get(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("ids argument is null");
		String sql = "select * from menu where id=?";
		return (Menu)this.baseDaoSupport.queryForObject(sql, Menu.class, id);
	}

	public Menu get(String title) {
		String sql = "select * from menu where title=?";
		List<Menu> menuList = this.baseDaoSupport.queryForList(sql, Menu.class,	title);

		if (menuList.isEmpty())
			return null;
		return menuList.get(0);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(Menu menu) {
		if (menu.getId() == null)
			throw new IllegalArgumentException("id argument is null");
		if (menu.getTitle() == null)
			throw new IllegalArgumentException("title argument is null");
		if (menu.getPid() == null)
			throw new IllegalArgumentException("pid argument is null");
		if (menu.getUrl() == null)
			throw new IllegalArgumentException("url argument is null");
		if (menu.getSorder() == null)
			throw new IllegalArgumentException("sorder argument is null");
		menu.setDeleteflag(0);
		this.baseDaoSupport.update("menu", menu, "id=" + menu.getId());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSort(Integer[] ids, Integer[] sorts) {
		if (ids == null)
			throw new IllegalArgumentException("ids argument is null");
		if (sorts == null)
			throw new IllegalArgumentException("sorts argument is null");
		if (sorts.length != ids.length)
			throw new IllegalArgumentException("ids's length and sorts's length not same");
		for (int i = 0; i < ids.length; i++) {
			String sql = "update menu set sorder=? where id=?";
			this.baseDaoSupport.execute(sql, sorts[i], ids[i]);
		}
	}

	public void delete(Integer id) throws RuntimeException {
		if (id == null)
			throw new IllegalArgumentException("ids argument is null");
		String sql = "select count(0) from menu where pid=?";
		int count = this.baseDaoSupport.queryForInt(sql, id);
		if (count > 0)
			throw new RuntimeException("菜单" + id + "存在子类别,不能直接删除，请先删除其子类别。");
		sql = "delete from menu where id=?";
		this.baseDaoSupport.execute(sql, id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(String title) {
		String sql = "delete from menu where title=?";
		this.baseDaoSupport.execute(sql, title);
	}
	

	@Override
	public void move(int menuid, int targetid, String type) {
		
		Menu menu = this.get(menuid);
		Menu target = this.get(targetid);
		
		int parentid = menu.getPid();
		int targetpid = target.getPid();
		
		//移入
		if("inner".equals(type)){
			
			this.baseDaoSupport.execute("update menu set pid=? where id=?", targetid,menu.getId());
			List<Integer> sorderList  = this.baseDaoSupport.queryForList("select max(sorder) sorder from menu where pid=?",new IntegerMapper(), targetid);//找到最大的排序
			int sorder=1;
			if(!sorderList.isEmpty()){
				sorder = sorderList.get(0)+1;
				
			}
			this.baseDaoSupport.execute("update menu set sorder=? where id=?", sorder,menuid);
			
		}
		
		//排序
		if("prev".equals(type) || "next".equals(type)){ //移动次序，但有可能切换了父菜单
			
			if(parentid!=targetpid){//切换了父菜单
				this.baseDaoSupport.execute("update menu set pid=? where id=?", targetpid,menu.getId());
			}
			
			if("prev".equals(type) ){
				
				//更新目标菜单所有上面的菜单排序-1		
				String sql  ="update menu set sorder=sorder-1 where pid=? and sorder<=? and id!=?";
				this.baseDaoSupport.execute(sql,targetpid,target.getSorder(),target.getId());
				
				//直接更新这个菜单的排序为目录菜单排序-1
				sql ="update menu set sorder=? where id=?";
				this.baseDaoSupport.execute(sql, target.getSorder()-1,menu.getId());
				
			}
			
			if("next".equals(type) ){
				
				//更新目标菜单所有上面的菜单排序-1		
				String sql  ="update menu set sorder=sorder+1 where pid=? and sorder>=? and id!=?";
				this.baseDaoSupport.execute(sql,targetpid,target.getSorder(),target.getId());
				
				//更新这个菜单的排序为目录菜单排序-1
				sql ="update menu set sorder=? where id=?";
				this.baseDaoSupport.execute(sql, target.getSorder()+1,menu.getId());
				
			}
			
		}
		
	}
	private IPermissionManager permissionManager;
	private String showall;
	private IAuthActionManager authActionManager;
	
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public String getShowall() {
		return showall;
	}

	public void setShowall(String showall) {
		this.showall = showall;
	}

	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}

	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}

	@Override
	public List<Menu> newMenutree(Integer menuid, AdminUser user) {
		if (menuid == null)
			throw new IllegalArgumentException("menuid argument is null");
		//获取所有菜单
		List<Menu> menuList = this.getMenuList();
		//对菜单进行筛选
		menuList=menuListByUser(menuList, user);
		//新的List
		List<Menu> topMenuList = new ArrayList<Menu>();
		for (Menu menu : menuList) {
			if (menu.getPid().compareTo(menuid) == 0) {
				List<Menu> children = this.getChildren(menuList, menu.getId());
				menu.setChildren(children);
				topMenuList.add(menu);
			}
		}
		return topMenuList;
	}
	private List<Menu> menuListByUser(List<Menu> menuList,AdminUser user){
		
		List<Menu> topMenuList = new ArrayList<Menu>();
		for (Menu menu:menuList) {
			List<AuthAction> authList = permissionManager.getUesrAct(user.getUserid(), "menu");
			for (AuthAction authAction:authList) {
				String arth[]= authAction.getObjvalue().split(",");
				for (int i = 0; i < arth.length; i++) {
					if (NumberUtils.toInt(arth[i]) == menu.getId() && choosemenu(topMenuList, menu)) {
						topMenuList.add(menu);
					}
				}
			}
		}
		return topMenuList;
	}
	private boolean choosemenu(List<Menu> newmenu, Menu menu){
		boolean choose=true;
		for (Menu cmenu:newmenu) {
			int menuId=menu.getId();
			int cmenuId=cmenu.getId();
			if(menuId==cmenuId){
				choose=false;
			}
		}
		return choose;
	}
}