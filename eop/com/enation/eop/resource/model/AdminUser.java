package com.enation.eop.resource.model;

import java.io.Serializable;
import java.util.List;

import com.enation.app.base.core.model.AuthAction;
import com.enation.framework.database.DynamicField;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 用户管理员
 * 
 * @author lzf
 *         <p>
 *         created_time 2009-11-27 下午01:40:54
 *         </p>
 * @version 1.0
 * 
 * @version 2.0 重构站点独立表，产且字段进行了变更
 */
@SuppressWarnings("serial")
public class AdminUser extends DynamicField implements Serializable {

	private Long userid;
	private String username;
	private String password;
	private int state;
	private String realname;
	private String userno;
	private String userdept;
	private String remark;
	private Long dateline;
	private int[] roleids;
	private int founder;
	private Integer siteid; // 子站点id

	private List<AuthAction> authList; // 此用户的权限点列表

	@PrimaryKeyField
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getUserdept() {
		return userdept;
	}

	public void setUserdept(String userdept) {
		this.userdept = userdept;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getDateline() {
		return dateline;
	}

	public void setDateline(Long dateline) {
		this.dateline = dateline;
	}

	public int getFounder() {
		return founder;
	}

	public void setFounder(int founder) {
		this.founder = founder;
	}

	@NotDbField
	public int[] getRoleids() {
		return roleids;
	}

	public void setRoleids(int[] roleids) {
		this.roleids = roleids;
	}

	public Integer getSiteid() {
		return siteid;
	}

	public void setSiteid(Integer siteid) {
		this.siteid = siteid;
	}

	@NotDbField
	public List<AuthAction> getAuthList() {
		return authList;
	}

	public void setAuthList(List<AuthAction> authList) {
		this.authList = authList;
	}

}
