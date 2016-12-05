package com.enation.app.base.core.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonUtil;
import com.gomecellar.workflow.utils.HttpClientUtils;

/**
 * 站点管理员管理
 * @author kingapex
 * 2010-11-5下午04:28:22新增角色管理
 * @author LiFenLong 2014-4-2;4.0改版
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("userAdmin")
@Results({
	@Result(name="success", type="freemarker", location="/core/admin/user/useradmin.html"), 
	@Result(name="add", type="freemarker", location="/core/admin/user/addUserAdmin.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/user/editUserAdmin.html"),
	@Result(name="editPassword",  location="editPassword.jsp")
})
public class UserAdminAction extends WWAction {
	@Value("#{configProperties['address.workflowUrl']}")
	public String workflowUrl;
	
	private AdminUserPluginBundle adminUserPluginBundle;
	private IAdminUserManager adminUserManager;
	private IRoleManager roleManager;
	private IPermissionManager permissionManager;
	private AdminUser adminUser;
	private Long id;
	private List roleList;
	private List userRoles;
	private int[] roleids;
	private List userList;
	private String newPassword; //新密码
	private String updatePwd;//是否修改密码
	private int multiSite;
	private List<String> htmlList;
	
	public String list() {
		userList= this.adminUserManager.list();
		return SUCCESS;
	}
	/**
	 * @author LiFenLong
	 * @return
	 */
	public String listJson(){
		userList= this.adminUserManager.list();
		this.showGridJson(userList);
		return JSON_MESSAGE;
	}

	public String add() throws Exception {
		
//		multiSite =EopContext.getContext().getCurrentSite().getMulti_site();
		roleList = roleManager.list();
		this.htmlList = this.adminUserPluginBundle.getInputHtml(null);
		return "add";
	}
	
	public String addSave() throws Exception {
		try{
			boolean flag = this.adminUserManager.is_exist(adminUser.getUsername());
			if(flag){
				this.showErrorJson("用户名已存在!");
			}else{
				adminUser.setRoleids(roleids);
				Long uid = adminUserManager.add(adminUser);
				String uname = adminUser.getUsername();
				String pass = adminUser.getPassword();
				String email = "";
				String suid = uid.toString();
				addUserSyn(adminUser,suid, uname, pass, email);
				this.showSuccessJson("新增管理员成功");
			}
		 } catch (RuntimeException e) {
			 this.showErrorJson("新增管理员失败");
			 logger.error("新增管理员失败", e);
		 }	
		return JSON_MESSAGE;
	}
	
    public boolean addUserSyn(AdminUser adminUser,String userId, String userName, String pwd, String email) {
        boolean rel = false;
        //参数
        Map<String,String> paramMapU = new HashMap<String,String>();
        paramMapU.put("userId",userId);     
        paramMapU.put("userName", userName); 
        paramMapU.put("pwd", pwd); 
        paramMapU.put("email", email); 
         
        //请求
        long t0 = System.currentTimeMillis();
        String resultU = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/addUser", paramMapU);
        System.out.println(resultU+", cost: "+(System.currentTimeMillis()-t0)+" ms");
        
        int[] roleids = adminUser.getRoleids();
        String resultG = "";
        for(int roleid:roleids){
            Map<String,String> paramMapG = new HashMap<String,String>();
            paramMapG.put("userId",userId);     
            paramMapG.put("groupId", String.valueOf(roleid)); 
            resultG = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/addUserGroup", paramMapG);
            System.out.println("同步添加用户角色"+resultG+", cost: "+(System.currentTimeMillis()-t0)+" ms");
        }
        
        //返回结果处理
        if ((!"".equals(resultU)) && (!"".equals(resultG))) {
            List<Map<String,Object>> list = JsonUtil.toList("[" + resultU + "]");
            String status = list.get(0).get("status").toString();
            if ("0".equals(status)) {
                System.out.println("同步添加用户成功");
                rel = true;
            }
        }
        return rel;
    }

	public String edit() throws Exception {
		
//		multiSite =EopContext.getContext().getCurrentSite().getMulti_site();
		roleList = roleManager.list();
		this.userRoles =permissionManager.getUserRoles(id);
		adminUser = this.adminUserManager.get(id);
		this.htmlList = this.adminUserPluginBundle.getInputHtml(adminUser);
		return "edit";
	}

	public String editSave() throws Exception {
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		try {
			if(updatePwd!=null){
				adminUser.setPassword(newPassword);
			}
			adminUser.setRoleids(roleids);
			this.adminUserManager.edit(adminUser);		
			this.showSuccessJson("修改管理员成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e,e.fillInStackTrace());
			this.showErrorJson("修改管理员失败");
		}

		return JSON_MESSAGE;
	}
	
    public boolean editUserSyn(String userId, String userName, String pwd, String email) {
        boolean rel = false;
        //参数
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("userId",userId);    
        paramMap.put("userName", userName); 
        paramMap.put("pwd", pwd);
        paramMap.put("email", email);
        
        //请求
        String result = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/updateUser", paramMap);
        System.out.println(result);
        
        for(int roleid:roleids){
            Map<String,String> paramMapG = new HashMap<String,String>();
            paramMapG.put("userId",String.valueOf(adminUser.getUserid()));     
            paramMapG.put("groupId", String.valueOf(roleid)); 
            String resultG = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/addUserGroup", paramMapG);
            System.out.println("同步修改用户角色");
        }
        
        //返回结果处理
        if (!"".equals(result)) {
            List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
            String status = list.get(0).get("status").toString();
            if ("0".equals(status)) {
                System.out.println("同步修改用户成功");
                rel = true;
            }
        }
        return rel;
    }
	

	public String delete() throws Exception {
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try {
			this.adminUserManager.delete(id);
			this.showSuccessJson("管理员删除成功");
			delUserSyn(String.valueOf(id));
		} catch (RuntimeException e) {
			this.showErrorJson("管理员删除失败");
			logger.error("管理员删除失败", e);
		}

		return JSON_MESSAGE;
	}
	
    public boolean delUserSyn(String userId) {
        boolean rel = false;
        //参数
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("userId",userId);       
        //请求
        long t0 = System.currentTimeMillis();
        String result = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/deleteUser", paramMap);
        System.out.println(result+", cost: "+(System.currentTimeMillis()-t0)+" ms");
        
        //返回结果处理
        if (!"".equals(result)) {
            List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
            String status = list.get(0).get("status").toString();
            if ("0".equals(status)) {
                System.out.println("同步删除用户成功");
                rel = true;
            }
        }
        return rel;
    }

	public String editPassword() throws Exception {
		return "editPassword";
	}



	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public AdminUser getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(AdminUser adminUser) {
		this.adminUser = adminUser;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}

	public List getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List userRoles) {
		this.userRoles = userRoles;
	}

	public int[] getRoleids() {
		return roleids;
	}

	public void setRoleids(int[] roleids) {
		this.roleids = roleids;
	}

	public List getUserList() {
		return userList;
	}

	public void setUserList(List userList) {
		this.userList = userList;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getUpdatePwd() {
		return updatePwd;
	}

	public void setUpdatePwd(String updatePwd) {
		this.updatePwd = updatePwd;
	}

	public int getMultiSite() {
		return multiSite;
	}

	public void setMultiSite(int multiSite) {
		this.multiSite = multiSite;
	}

	public AdminUserPluginBundle getAdminUserPluginBundle() {
		return adminUserPluginBundle;
	}

	public void setAdminUserPluginBundle(AdminUserPluginBundle adminUserPluginBundle) {
		this.adminUserPluginBundle = adminUserPluginBundle;
	}

	public List<String> getHtmlList() {
		return htmlList;
	}

	public void setHtmlList(List<String> htmlList) {
		this.htmlList = htmlList;
	}
 
	
}
