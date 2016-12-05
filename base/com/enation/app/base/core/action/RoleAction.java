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

import com.enation.app.base.core.model.Role;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonUtil;
import com.gomecellar.workflow.utils.Constants;
import com.gomecellar.workflow.utils.HttpClientUtils;

/**
 * 角色管理
 * @author kingapex
 * 2010-11-4下午05:25:48
 * @author LiFenLong 2014-4-2;4.0版本改造
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("role")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/auth/rolelist.html"),
	@Result(name="edit", type="freemarker", location="/core/admin/auth/role_edit.html"), 
	@Result(name="add", type="freemarker", location="/core/admin/auth/role_add.html") 
})
public class RoleAction extends WWAction {
	@Value("#{configProperties['address.workflowUrl']}")
	public String workflowUrl;
	
	private IRoleManager roleManager;
	private IAuthActionManager authActionManager;
	
	private List roleList;
	private List authList;
	private int roleid;
	private Role role;
	private int[] acts;
	private int isEdit;
	
	
	//读取角色列表
	public String list(){
		roleList = roleManager.list();
		return "list";
	}
	public String listJson(){
		roleList = roleManager.list();
		this.showGridJson(roleList);
		return this.JSON_MESSAGE;
	}
	
	
	//到添加页面
	public String add(){
		authList = authActionManager.list();
		return "add";
	}
	
	
	//到修改页面
	public String edit(){
		authList = authActionManager.list();
		isEdit= 1;
		this.role = this.roleManager.get(roleid);
		return "edit";
	}
	
	
	
	//保存添加
	public String saveAdd(){	
		try {
		    System.out.println("test");
			int gid = this.roleManager.add(role, acts);
			//addUpdateGroudSyn(String.valueOf(gid),role.getRolename(),"addGroup");
			this.showSuccessJson("新增角色成功");
		} catch (Exception e) {
			this.showErrorJson("新增角色失败");
			logger.error("新增角色失败", e);
		}
		return this.JSON_MESSAGE;
	}
	
	
    public boolean addUpdateGroudSyn(String groupId, String groupName,String act) {
        boolean rel = false;
        //参数
        Map<String,String> paramMapG = new HashMap<String,String>();
        paramMapG.put("groupId",groupId);     
        paramMapG.put("groupName", groupName); 
        
        //请求
        String resultG = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/"+act, paramMapG);
        System.out.println("添加用户角色"+groupName+resultG);
        //返回结果处理
        if ((!"".equals(resultG)) && (!"".equals(resultG))) {
            List<Map<String,Object>> list = JsonUtil.toList("[" + resultG + "]");
            String status = list.get(0).get("status").toString();
            if ("0".equals(status)) {
                rel = true;
            }
        }
        return rel;
    }
	
	//保存修改
	public String saveEdit(){
		try {
			this.roleManager.edit(role, acts);
			addUpdateGroudSyn(String.valueOf(role.getRoleid()),role.getRolename(),"updateGroup");
			this.showSuccessJson("角色修改成功");
		} catch (Exception e) {
			this.showErrorJson("角色修改失败");
			logger.error("角色修改失败", e);
		}		
		return this.JSON_MESSAGE;
	}
	
	//删除角色
	public String delete(){
		
		if(EopSetting.IS_DEMO_SITE){
			if(roleid<=5){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		
		try {
			this.roleManager.delete(roleid);
			
			this.showSuccessJson("角色删除成功");
			deleteGroudSyn(String.valueOf(roleid));
		} catch (Exception e) {
			this.showErrorJson("角色删除失败");
			logger.error("角色删除失败", e);
		}		
		return this.JSON_MESSAGE;
	}
	
    public boolean deleteGroudSyn(String groupId) {
        boolean rel = false;
        //参数
        Map<String,String> paramMapG = new HashMap<String,String>();
        paramMapG.put("groupId",groupId);     
 
        //请求
        String resultG = HttpClientUtils.get(workflowUrl+ "/restful/userSyn/deleteGroup", paramMapG);
        System.out.println("删除用户角色"+resultG);
        //返回结果处理
        if ((!"".equals(resultG)) && (!"".equals(resultG))) {
            List<Map<String,Object>> list = JsonUtil.toList("[" + resultG + "]");
            String status = list.get(0).get("status").toString();
            if ("0".equals(status)) {
                rel = true;
            }
        }
        return rel;
    }
	
	
	public IRoleManager getRoleManager() {
		return roleManager;
	}
	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}



	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}



	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}



	public List getRoleList() {
		return roleList;
	}



	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}



	public List getAuthList() {
		return authList;
	}



	public void setAuthList(List authList) {
		this.authList = authList;
	}



	public int getRoleid() {
		return roleid;
	}



	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}



	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public int[] getActs() {
		return acts;
	}
	public void setActs(int[] acts) {
		this.acts = acts;
	}

	public int getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(int isEdit) {
		this.isEdit = isEdit;
	}
	
	
}
