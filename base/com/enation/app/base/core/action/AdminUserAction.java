package com.enation.app.base.core.action;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.StringUtil;

/**
 * 管理员action
 * @author kingapex
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("adminUser")
@Results({
})
@SuppressWarnings("serial")
public class AdminUserAction extends WWAction {
	private IAdminUserManager adminUserManager;
	private String username;
	private String password;
	private String valid_code;
	private String remember_login_name;
	
	
	
	public String login(){
		try {
			/*
			 * 校验验证码
			 */
			if (valid_code == null)
			{
				this.showErrorJson("验证码输入错误");
				return this.JSON_MESSAGE;
			}
			valid_code = valid_code.toLowerCase();
			WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
			Object realCode = ("" + sessonContext.getAttribute(ValidCodeServlet.SESSION_VALID_CODE + "admin")).toLowerCase();

			if (!valid_code.equals(realCode)) {
				this.showErrorJson("验证码输入错误");
			 
				return this.JSON_MESSAGE;
			}

			/*
			 * 登录校验
			 */
			 
			adminUserManager.login(username, password);

		 
			HttpServletResponse httpResponse = ThreadContextHolder.getHttpResponse();
			 
			if (!StringUtil.isEmpty(remember_login_name)) { // 记住用户名
				HttpUtil.addCookie(httpResponse, "loginname", username,	365 * 24 * 60 * 60);
			} else { // 删除用户名
				HttpUtil.addCookie(httpResponse, "loginname", "", 0);
			}
			 this.showSuccessJson("登录成功");
		} catch (Throwable exception) {
		 
			this.logger.error(exception.getMessage(), exception);
			this.showErrorJson(exception.getMessage());
		 
		}
		return this.JSON_MESSAGE;
	}
	
	public String logout(){
		try {
		    WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
	        sessonContext.removeAttribute(UserConext.CURRENT_ADMINUSER_KEY);
        } catch(Exception e) { 
        }
		this.showSuccessJson("成功注销");
		return this.JSON_MESSAGE;
	}
	
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getValid_code() {
		return valid_code;
	}
	public void setValid_code(String valid_code) {
		this.valid_code = valid_code;
	}

	public String getRemember_login_name() {
		return remember_login_name;
	}

	public void setRemember_login_name(String remember_login_name) {
		this.remember_login_name = remember_login_name;
	}
	
	
}
