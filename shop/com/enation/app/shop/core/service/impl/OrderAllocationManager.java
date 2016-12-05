package com.enation.app.shop.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IOrderAllocationManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.impl.IRowMapperColumnFilter;
import com.enation.framework.util.StringUtil;

/**
 * 
 * @author dable
 *
 */
public class OrderAllocationManager extends BaseSupport implements
		IOrderAllocationManager {

	private OrderPluginBundle orderPluginBundle;

	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	@Override
	public List listAllocation(int orderid) {
//		String sql = "select l.*,ed.name as sp_name,'' as addon from (select g.sn,g.name as gname,g.cat_id,d.name as a_name,o.depotid as dpid, a.* from "
//				+ this.getTableName("allocation_item")
//				+ "  a left join "
//				+ this.getTableName("goods")
//				+ " g on g.goods_id = a.goodsid  left join "
//				+ this.getTableName("depot")
//				+ "  d on d.id= a.depotid left join "
//				+ this.getTableName("order")
//				+ " o on o.order_id = a.orderid where a.orderid=?) as l  left join "
//				+ this.getTableName("depot") + " ed on l.dpid = ed.id";
		
		String sql ="select oi.sn sn ,oi.name  gname,oi.price price,a.iscmpl iscmpl,a.num num ,d.name a_name ,oi.addon addon,oi.cat_id cat_id,oi.image image" ;
		sql+=" from    "+this.getTableName("allocation_item")+" a ";
		sql+=" LEFT JOIN "+this.getTableName("order_items")+" oi on a.itemid= oi.item_id";
		sql+=" LEFT JOIN "+this.getTableName("depot")+" d on a.depotid= d.id ";
		sql+=" where a.orderid=?";
		 
		////System.out.println(sql);
		IRowMapperColumnFilter columnFilter = new IRowMapperColumnFilter() {
			@Override
			public void filter(Map colValues, ResultSet rs) throws SQLException {
				int catid = rs.getInt("cat_id");
				orderPluginBundle.filterAlloItem(catid, colValues, rs);
			}
		};

		return this.daoSupport.queryForList(sql, columnFilter, orderid);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean(Integer[] ids){
		if (ids == null)
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from "+this.getTableName("allocation_item")+" where orderid in ("
				+ id_str + ")";
		this.baseDaoSupport.execute(sql);
	}
}
