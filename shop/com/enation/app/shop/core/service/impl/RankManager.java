package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.service.IRankManager;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;

public class RankManager extends BaseSupport implements IRankManager {

	
	public List rank_goods(int page, int pageSize, String condition, String sort) {
		String sql = "select items.name name, sum(items.price * items.num) amount," +
				" sum(items.num) num from " + this.getTableName("order") + " orders , " +
				this.getTableName("order_items") + " items where items.order_id = orders.order_id";

		if(condition == null || condition.equals("")){
			condition = " and orders.status = 3";
		}else{
			condition += " and orders.status = 3";
		}
		sql += condition;
		sql += " group by name,num";
		sort = sort == null ? "num desc" : sort;
		sql += " order by " + sort;
		////System.out.println(sql);
		List list = this.daoSupport.queryForListPage(sql, page, pageSize);
		return list;
	}

	
	public List rank_member(int page, int pageSize, String condition,
			String sort) {
		List list = null;
		String sql = "select member.uname uname, member.name name, sum(items.price * items.num) amount, sum(items.num) num from " + this.getTableName("order") + " orders , " + this.getTableName("order_items") + " items, " + this.getTableName("member") + " member where items.order_id = orders.order_id and member.member_id = orders.member_id";
		if(condition == null || condition.equals("")){
			condition = " and orders.status = 3";
		}else{
			condition += " and orders.status = 3";
		}
		sql += condition;
		sql += " group by member.uname, member.name";
		sort = sort == null ? "sum(items.num) desc" : sort;
		sql += " order by " + sort;
		if("3".equals(EopSetting.DBTYPE)) {	//SQLServerorder by sum(...)时，必须指定top
			list = this.daoSupport.queryForList("select * from (select top " + pageSize + " ROW_NUMBER() Over(order by sum(items.num) desc) as rowNum," + sql.substring(6) + ") tb");
		} else {
			list = this.daoSupport.queryForListPage(sql, page, pageSize);
		}
		return list;
	}

	
	public List rank_buy(int page, int pageSize, String sort) {
		String mysql = "select name, view_count, buy_count, FORMAT(buy_count / (case view_count when null then 1 when 0 then 1 else view_count end)*100, 2) per from goods where disabled = 0 and buy_count > 0";
		String sqlserver = "select name, view_count, buy_count, Round(buy_count / (case view_count when null then 1 when 0 then 1 else view_count end)*100, 2) per from goods where disabled = 0 and buy_count > 0";
		String groupby = " group by name,view_count,buy_count";
		mysql += groupby;
		sqlserver += groupby;
		sort = sort == null ? "view_count desc" : sort;
		sort = " order by " + sort;
		mysql += sort;
		sqlserver += sort;
		List list = null;
		if("1".equals(EopSetting.DBTYPE))
			list = this.baseDaoSupport.queryForListPage(mysql, page, pageSize);
		else if("3".equals(EopSetting.DBTYPE))
			list = this.baseDaoSupport.queryForListPage(sqlserver, page, pageSize);
		return list;
	}

	
	public Map rank_all() {
		Map map = this.baseDaoSupport.queryForMap("select sum(order_amount) amount, count(order_id) num from order where status = 3");
		int view_count = this.baseDaoSupport.queryForInt("select sum(view_count) view_count from goods where disabled = 0");
		map.put("view_count", view_count);
		int member_count = this.baseDaoSupport.queryForInt("select count(member_id) member_count from member");
		map.put("member_count", member_count);
		int buy_member_count = this.baseDaoSupport.queryForInt("select count(distinct member_id) buy_member_count from order where status = 3 and member_id > 0");
		map.put("buy_member_count", buy_member_count);
		return map;
	}

}
