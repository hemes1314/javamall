package com.enation.app.base.core.service.auth.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.StringUtil;

/**
 * 权限点管理
 * 
 * @author kingapex 2010-10-24下午10:38:33
 */
public class AuthActionManager extends BaseSupport<AuthAction> implements IAuthActionManager {

	@Transactional(propagation = Propagation.REQUIRED)
	public int add(AuthAction act) {
		this.baseDaoSupport.insert("auth_action", act);
		return this.baseDaoSupport.getLastId("auth_action");
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(int actid) {
		// 删除角色权限表中对应的数据
		this.baseDaoSupport.execute("delete from role_auth where authid=?",	actid);
		// 删除权限基本数据
		this.baseDaoSupport.execute("delete from auth_action where actid=?", actid);
	}

	public void edit(AuthAction act) {
		this.baseDaoSupport.update("auth_action", act, "actid=" + act.getActid());
	}

	public List<AuthAction> list() {
		return this.baseDaoSupport.queryForList("select * from auth_action where actid!=0", AuthAction.class);
	}

	public AuthAction get(int authid) {
		// return
		// this.baseDaoSupport.queryForObject("select * from auth_action where actid=?",
		// AuthAction.class, authid);
		// 修改此方法，解决log中的大量报错
		List<AuthAction> list = this.baseDaoSupport.queryForList("select * from auth_action where actid=?", AuthAction.class, authid);
		AuthAction result = null;
		if (list.size() > 0)
			result = list.get(0);
		return result;
	}

	@Override
	public void addMenu(int actid, Integer[] menuidAr) {
		if (menuidAr == null)
			return;

		AuthAction authAction = this.get(actid);
		if (authAction == null)
			return;
		String menuStr = authAction.getObjvalue();
		if (StringUtil.isEmpty(menuStr)) {
			menuStr = StringUtil.arrayToString(menuidAr, ",");
			authAction.setObjvalue(menuStr);
		} else {
			String[] oldMenuAr = StringUtils.split(menuStr, ",");// menuStr.split(",");
			oldMenuAr = merge(menuidAr, oldMenuAr);
			menuStr = StringUtil.arrayToString(oldMenuAr, ",");
			authAction.setObjvalue(menuStr);
		}
		this.edit(authAction);
	}

	@Override
	public void deleteMenu(int actid, Integer[] menuidAr) {
		if (menuidAr == null)
			return;
		AuthAction authAction = this.get(actid);
		if (authAction == null)
			return;

		String menuStr = authAction.getObjvalue();
		if (StringUtil.isEmpty(menuStr)) {
			return;
		}

		String[] oldMenuAr = StringUtils.split(menuStr, ",");
		menuStr.split(",");
		oldMenuAr = delete(menuidAr, oldMenuAr);
		menuStr = StringUtil.arrayToString(oldMenuAr, ",");
		authAction.setObjvalue(menuStr);
		this.edit(authAction);
	}

	/**
	 * 将ar1合并进ar2中
	 * 
	 * @param ar1
	 * @param ar2
	 * @return
	 */
	private static String[] merge(Integer[] ar1, String[] ar2) {

		List<String> newList = new ArrayList<String>();
		for (String num : ar2) {
			newList.add(num);
		}

		boolean flag = false;
		for (Integer num1 : ar1) {
			flag = false;

			for (String num2 : ar2) {
				if (num1.intValue() == Integer.valueOf(num2)) {
					flag = true;
					break;
				}
			}

			if (!flag) {// 原数组不存在这个数添加进来
				newList.add(String.valueOf(num1));
			}
		}

		return (String[]) newList.toArray(new String[newList.size()]);
	}

	/**
	 * 从ar2中删除a1
	 * 
	 * @param ar1
	 * @param ar2
	 * @return
	 */
	public static String[] delete(Integer[] ar1, String[] ar2) {
		List<String> newList = new ArrayList<String>();
		boolean flag = false;
		for (String num2 : ar2) {
			flag = false;
			for (Integer num1 : ar1) {
				if (num1.intValue() == Integer.valueOf(num2)) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				newList.add(num2);
			}
		}

		return (String[]) newList.toArray(new String[newList.size()]);
	}

}
