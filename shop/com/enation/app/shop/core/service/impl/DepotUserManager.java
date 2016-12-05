package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.service.IDepotUserManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;

public class DepotUserManager extends BaseSupport<DepotUser> implements
		IDepotUserManager {

	@Override
	public void add(DepotUser depotUser) {
		this.daoSupport.execute("insert into es_depot_user(userid,depotid)values(?,?)", depotUser.getUserid(),depotUser.getDepotid());
	}

	@Override
	public void edit(DepotUser depotUser) {
		this.daoSupport.execute("update es_depot_user set roomid=? where userid=?", depotUser.getDepotid(),depotUser.getUserid());
	}

	@Override
	public void delete(int userid) {
		this.daoSupport.execute("delete from es_depot_user where userid=?", userid);
	}

	@Override
	public DepotUser get(int userid) {
		String sql = "select * from es_depot_user where userid=?";
		return this.daoSupport.queryForObject(sql, DepotUser.class, userid);
	}

	@Override
	public List<DepotUser> listByDepotId(int depotid) {
		String sql = "select * from es_depot_user where depotid=?";
		return this.daoSupport.queryForList(sql, DepotUser.class, depotid);
	}
	
	public List<DepotUser> listByRoleId(int roleid){
		
		String sql  ="select u.*,du.depotid from "+ this.getTableName("adminuser") +" u ," + this.getTableName("user_role") + " ur, "+this.getTableName("depot_user")+" du  where ur.userid=u.userid and du.userid = u.userid and ur.roleid=?";
		return this.daoSupport.queryForList(sql, DepotUser.class, roleid);
	}
	
	public List<DepotUser> listByRoleId(int depotid,int roleid){
		String sql  ="select u.*,du.depotid from "+ this.getTableName("adminuser") +" u ," + this.getTableName("user_role") + " ur, "+this.getTableName("depot_user")+" du  where ur.userid=u.userid and du.userid = u.userid and  du.depotid=? and ur.roleid=?";
		return this.daoSupport.queryForList(sql, DepotUser.class,depotid, roleid);
	}
	
}
