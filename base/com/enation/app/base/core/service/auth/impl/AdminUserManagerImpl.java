package com.enation.app.base.core.service.auth.impl;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.util.StringUtil;

/**
 * 管理员管理实现
 * 
 * @author kingapex 2010-11-5下午06:49:02
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdminUserManagerImpl extends BaseSupport<AdminUser> implements	IAdminUserManager {
	private AdminUserPluginBundle adminUserPluginBundle;
	private IPermissionManager permissionManager;

	public void clean() {
		this.baseDaoSupport.execute("truncate table adminuser");
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Long add(AdminUser adminUser) {
		adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
		// 添加管理员
		this.baseDaoSupport.insert("adminuser", adminUser);
		long userid = this.baseDaoSupport.getLastId("adminuser");

		// 给用户赋予角色
		permissionManager.giveRolesToUser(userid, adminUser.getRoleids());
		this.adminUserPluginBundle.onAdd(userid);
		return userid;
	}

	/**
	 * 为某个站点添加管理员
	 * 
	 * @param userid
	 * @param siteid
	 * @param adminUser
	 * @return 添加的管理员id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Long add(long userid, int siteid, AdminUser adminUser) {
		adminUser.setState(1);
		this.baseDaoSupport.insert("adminuser", adminUser);
		return this.baseDaoSupport.getLongLastId("adminuser");
	}

	public int checkLast() {
		int count = this.baseDaoSupport.queryForInt("select count(0) from adminuser");
		return count;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long id) {
		// 如果只有一个管理员，则抛出异常
		if (this.checkLast() == 1) {
			throw new RuntimeException("必须最少保留一个管理员");
		}

		// 清除用户角色
		permissionManager.cleanUserRoles(id);

		// 删除用户基本信息
		this.baseDaoSupport.execute("delete from adminuser where userid=?", id);
		this.adminUserPluginBundle.onDelete(id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(AdminUser adminUser) {
		// 给用户赋予角色
		permissionManager.giveRolesToUser(adminUser.getUserid(), adminUser.getRoleids());

		// 修改用户基本信息
		if (!StringUtil.isEmpty(adminUser.getPassword()))
			adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
		long userId = adminUser.getUserid();
		adminUser.setUserid(null); // 不设置为空，SQLServer更新出错
		this.baseDaoSupport.update("adminuser", adminUser, "userid=" + userId);
		this.adminUserPluginBundle.onEdit(userId);
	}

	public AdminUser get(Long id) {
		return this.baseDaoSupport.queryForObject("select * from adminuser where userid=?", AdminUser.class, id);
	}

	public List list() {
		return this.baseDaoSupport.queryForList("select * from adminuser order by dateline", AdminUser.class);
	}

	public List<Map> list(Long userid, Integer siteid) {
		String sql = "select * from es_adminuser_" + userid + "_" + siteid;
		return this.daoSupport.queryForList(sql);
	}

	public List<AdminUser> listByRoleId(int roleid) {
		String sql = "select u.* from " + this.getTableName("adminuser")
				+ " u ," + this.getTableName("user_role")
				+ " ur where ur.userid=u.userid and ur.roleid=?";
		return this.daoSupport.queryForList(sql, AdminUser.class, roleid);
	}

	/**
	 * 管理员登录
	 * 
	 * @param username
	 * @param password
	 *            未经过md5加密的密码
	 * @return 登录成功返回管理员
	 * @throws RuntimeException
	 *             当登录失败时抛出此异常，登录失败的原因可通过getMessage()方法获取
	 */
	public long login(String username, String password) {
		return this.loginBySys(username, StringUtil.md5(password));
	}

	/**
	 * 系统登录
	 * 
	 * @param username
	 * @param password
	 *            此处为未经过md5加密的密码
	 * @return 返回登录成功的用户id
	 * @throws RuntimeException
	 *             登录失败抛出此异常，登录失败原因可通过getMessage()方法获取
	 */
	public long loginBySys(String username, String password) {
		String sql = "select * from adminuser where username=?";
		List<AdminUser> userList = this.baseDaoSupport.queryForList(sql, AdminUser.class, username);
		if (userList == null || userList.size() == 0) {
			throw new RuntimeException("此用户不存在");
		}
		AdminUser user = userList.get(0);

		if (!password.equals(user.getPassword())) {
			throw new RuntimeException("密码错误");
		}

		if (user.getState() == 0) {
			throw new RuntimeException("此用户已经被禁用");
		}

	 

		// 读取此用户的权限点，并设置给当前用户
		List<AuthAction> authList = this.permissionManager.getUesrAct(user.getUserid());
		user.setAuthList(authList);
 

		// 记录session信息
		WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
		sessonContext.setAttribute(UserConext.CURRENT_ADMINUSER_KEY, user);
		this.adminUserPluginBundle.onLogin(user);
		return user.getUserid();
	}

	@Override
	public boolean is_exist(String username) {
		boolean flag = false;
		int i =  this.daoSupport.queryForInt("select count(0) from es_adminuser where username=?", username);
		if(i!=0){
			flag=true;
		}
		return flag;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public AdminUserPluginBundle getAdminUserPluginBundle() {
		return adminUserPluginBundle;
	}

	public void setAdminUserPluginBundle(
			AdminUserPluginBundle adminUserPluginBundle) {
		this.adminUserPluginBundle = adminUserPluginBundle;
	}

	

}
