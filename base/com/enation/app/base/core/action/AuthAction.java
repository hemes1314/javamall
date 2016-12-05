package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.framework.action.WWAction;

/**
 * 权限点
 * @author kingapex
 * 2010-11-9下午05:55:11
 * @author LiFenLong 2014-6-4;4.0改版
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("auth")
@Results({
	@Result(name="input", type="freemarker", location="/core/admin/auth/auth_input.html"),
	@Result(name="list", type="freemarker", location="/core/admin/auth/auth_list.html")
})
public class AuthAction extends WWAction {
	private String name;
	private int authid;
	private int isEdit;
	private IAuthActionManager authActionManager;
	private String menuids;
	private com.enation.app.base.core.model.AuthAction auth;
	
	public String add(){
		isEdit=0;
		return "input";
	} 
	
	public String edit(){
		isEdit=1;
		auth =this.authActionManager.get(authid);
		return "input";
	}
	

	public String saveEdit(){
		try{
			com.enation.app.base.core.model.AuthAction act = new com.enation.app.base.core.model.AuthAction();
			act.setName(name);
			act.setType("menu");
			act.setActid(authid);
			
			/*add by linyang 权限点为空判断*/
            if(menuids.equals(""))
            {
                this.showErrorJson("修改失败，请选择权限点");
                return this.JSON_MESSAGE;
            }
			
			act.setObjvalue(menuids);
			this.authActionManager.edit(act);
			this.showSuccessJson("修改成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e.fillInStackTrace());
			this.showErrorJson("修改失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	public String saveAdd(){
		try{
		    /*add by linyang 权限点为空判断*/
		    if(menuids.equals(""))
		    {
		        this.showErrorJson("添加失败，请选择权限点");
		        return this.JSON_MESSAGE;
		    }
			com.enation.app.base.core.model.AuthAction act = new com.enation.app.base.core.model.AuthAction();
			act.setName(name);
			act.setType("menu");
			
			act.setObjvalue(menuids);
			authid = this.authActionManager.add(act);
			this.showSuccessJson("添加成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e.fillInStackTrace());
			this.showErrorJson("添加失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	public String delete(){
		try{
			com.enation.app.base.core.model.AuthAction authaction=this.authActionManager.get(authid);
			if(authaction.getChoose()==0){
				this.authActionManager.delete(this.authid);
				this.showSuccessJson("删除成功");
			}
			else{
				this.showErrorJson("此权限点为系统默认权限点，不能进行删除!");
			}
		}catch(RuntimeException e){
			this.showErrorJson("删除失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	public String list(){
		return "list";
	}
	public String listJson(){
		this.showGridJson(authActionManager.list()) ;
		return this.JSON_MESSAGE;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAuthid() {
		return authid;
	}

	public void setAuthid(int authid) {
		this.authid = authid;
	}

	public int getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(int isEdit) {
		this.isEdit = isEdit;
	}

	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}

	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}

	public String getMenuids() {
		return menuids;
	}

	public void setMenuids(String menuids) {
		this.menuids = menuids;
	}

	public com.enation.app.base.core.model.AuthAction getAuth() {
		return auth;
	}

	public void setAuth(com.enation.app.base.core.model.AuthAction auth) {
		this.auth = auth;
	}
}
