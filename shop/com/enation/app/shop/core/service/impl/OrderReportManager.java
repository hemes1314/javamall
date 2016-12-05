package com.enation.app.shop.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.PaymentLogType;
import com.enation.app.shop.core.model.RefundLog;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.Page;
import com.enation.framework.database.impl.IRowMapperColumnFilter;
import com.enation.framework.util.StringUtil;

/**
 * 订单报表
 * 
 * @author lzf<br/>
 *         2010-4-12上午10:23:43<br/>
 *         version 1.0
 */
public class OrderReportManager extends BaseSupport implements
		IOrderReportManager {
	
	private IAdminUserManager adminUserManager;
	private OrderPluginBundle orderPluginBundle;
	private IPermissionManager permissionManager;

	
	
	
	public Delivery getDelivery(Integer deliveryId) {
		String sql = "select l.*, m.uname as member_name, o.sn from " + this.getTableName("delivery") + " l left join " + this.getTableName("member") + " m on m.member_id = l.member_id left join " + this.getTableName("order") + " o on o.order_id = l.order_id where l.delivery_id = ?";
		Delivery delivery = (Delivery)this.daoSupport.queryForObject(sql, Delivery.class, deliveryId);
		return delivery;
	}
	
	public List<Delivery> getDeliveryList(int orderId){
		String sql = "select * from " + this.getTableName("delivery") + " where order_id="+orderId;		
		return this.daoSupport.queryForList(sql,Delivery.class);
	}

	
	public PaymentLog getPayment(Integer paymentId) {
		String sql = "select l.*, m.uname as member_name, o.sn from " + this.getTableName("payment_logs") + " l left join " + this.getTableName("member") + " m on m.member_id = l.member_id left join " + this.getTableName("order") + " o on o.order_id = l.order_id where l.payment_id = ?";
		PaymentLog paymentLog = (PaymentLog)this.daoSupport.queryForObject(sql, PaymentLog.class, paymentId);
		return paymentLog;
	}

	public RefundLog getRefund(Integer refundid){
		String sql  ="select * from refund_logs where refund_id=?" ;
		RefundLog refundLog=(RefundLog) this.baseDaoSupport.queryForObject(sql, RefundLog.class, refundid);
		return refundLog;
	}
	public List<DeliveryItem> listDeliveryItem(Integer deliveryId) {
		String sql = "select * from delivery_item where delivery_id = ?";
		return this.baseDaoSupport.queryForList(sql, DeliveryItem.class, deliveryId);
	}

	
	/**
	 * 分页读取收款单
	 */
	public Page listPayment(Map map,int pageNo, int pageSize, String order) {
		String sql = createTempSql(map);
		return this.baseDaoSupport.queryForPage(sql, pageNo, pageSize);		
	}
	@SuppressWarnings("unused")
	private String  createTempSql(Map map){
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer payment_id = (Integer) map.get("payment_id");
		
		String sql = "select * from payment_logs where payment_id>0 and type="+PaymentLogType.receivable.getValue();
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and sn like '%"+keyword+"%'";
				sql+=" or ship_name like '%"+keyword+"%'";
			}
		}
		
		if(sn!=null && !StringUtil.isEmpty(sn)){
			sql+=" and order_sn like '%"+sn+"%'";
		}
		
		if(paystatus!=null){
			sql+=" and status="+paystatus;
		}
		
		if(payment_id!=null){
			sql+="and order_id in(select order_id from es_order where payment_id="+payment_id+")";
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and create_time>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and create_time<"+(etime*1000);
		}
		
		sql += " order by payment_id desc";
		////System.out.println(sql);
		return sql;
	}
	
	/**
	 * 读取某个订单的收款单
	 */
	public List<PaymentLog> listPayLogs(Integer orderId) {
		return this.baseDaoSupport.queryForList("select * from payment_logs where order_id = ? ",PaymentLog.class, orderId);
	}
	
	
	
	/**
	 * 分页读取退款单
	 */
	public Page listRefund(int pageNo, int pageSize, String order) {
		order = (order == null) ? "l.payment_id desc" : order ;
		String sql ="select * from refund_logs ";
		return this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, RefundLog.class);
	}
	
	
	/**
	 * 读取某个订单的退款单
	 */
	public List<RefundLog> listRefundLogs(Integer order_id){
		
		return this.baseDaoSupport.queryForList("select * from refund_logs where order_id = ? ",RefundLog.class, order_id);
	}
	
	
	private Page listDelivery(int pageNo, int pageSize, String order, int type){
		order = (order == null) ? "delivery_id desc" : order ;
		String sql = "select l.*, m.uname as member_name, o.sn from " + this.getTableName("delivery") + " l left join " + this.getTableName("member") + " m on m.member_id = l.member_id left join " + this.getTableName("order") + " o on o.order_id = l.order_id where l.type = " + type ;
		sql += " order by " + order;
		////System.out.println(sql);
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, Delivery.class);
	}

	
	public Page listReturned(int pageNo, int pageSize, String order) {
		String sql = "select * from sellback_list order by id desc ";
		Page webpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize);
		return webpage;
	}

	
	public Page listShipping(int pageNo, int pageSize, String order) {
		return listDelivery(pageNo, pageSize, order, 1);
	}

	
	public List listDelivery(Integer orderId, Integer type) {
		return this.baseDaoSupport.queryForList("select * from delivery where order_id = ? and type = ?", orderId, type);
	}

	



	@Override
	public Page listAllocation(int pageNo, int pageSize) {
		DepotUser depotUser = (DepotUser)UserConext.getCurrentAdminUser();
		int depotid = depotUser.getDepotid(); //当前会员所属仓库
		String sql = "select o.sn,o.status,a.*,i.name as name,i.cat_id,i.addon  from  "+ this.getTableName("allocation_item")  +" a left join "+this.getTableName("order_items") +" i on a.itemid= i.item_id left join  "
				
					+ this.getTableName("order") +" o on a.orderid= o.order_id where a.depotid=?  order by o.create_time desc" ; 
		////System.out.println(sql);
		IRowMapperColumnFilter columnFilter = new IRowMapperColumnFilter() {
			
			@Override
			public void filter(Map colValues, ResultSet rs) throws SQLException {
					int catid = rs.getInt("cat_id");
					orderPluginBundle.filterAlloItem(catid, colValues, rs); 
				
			}
			
		};
		
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,columnFilter, depotid);
	}
	
	@Override
	public Page listAllocation(int depotid,int status,int pageNo, int pageSize) {
		
 
		
		
		String sql = "select o.sn,o.status,a.*,i.name as name,i.cat_id,i.addon  from  "+ this.getTableName("allocation_item")  +" a left join "+this.getTableName("order_items") +" i on a.itemid= i.item_id left join  "
					+ this.getTableName("order") +" o on a.orderid= o.order_id  where  a.iscmpl=? " ; 
		
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));//超级管理员权限
		if(!isSuperAdmin){
			DepotUser depotUser = (DepotUser)UserConext.getCurrentAdminUser();
			depotid = depotUser.getDepotid(); //当前会员所属仓库
			sql+=" and a.depotid="+depotid;
		}
		sql+=" order by o.create_time desc" ;
		
		////System.out.println(sql);
		IRowMapperColumnFilter columnFilter = new IRowMapperColumnFilter() {
			
			@Override
			public void filter(Map colValues, ResultSet rs) throws SQLException {
					int catid = rs.getInt("cat_id");
					orderPluginBundle.filterAlloItem(catid, colValues, rs); 
				
			}
			
		};
		
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,columnFilter, status);
	}

	@Override
	public double getPayMoney(Integer paymentId){
		return  (Double)this.baseDaoSupport.queryForObject("select sum(pay_money) from payment_detail where payment_id=?",new DoubleMapper(), paymentId);
	}
	/**
	 * 添加某订单详细支付单
	 * @author LiFenLong
	 * 
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addPayMentDetail(PaymentDetail paymentdetail){
		this.baseDaoSupport.insert("payment_detail", paymentdetail);
	}
	/**
	 * 获取订单下的收款单Id
	 * @author LiFenLong
	 */
	public Integer getPaymentLogId(Integer orderId) {
		
		return this.baseDaoSupport.queryForInt("select payment_id from payment_logs where order_id=? and type="+PaymentLogType.receivable.getValue(), orderId);
	}
	/** 
	 * 读取某个订单的收款详细单
	 * @author LiFenLong
	 * @return list<PaymentDetail>
	 */
	public List<PaymentDetail> listPayMentDetail(Integer paymentId){
		
		return this.baseDaoSupport.queryForList("select * from payment_detail where payment_id=?",PaymentDetail.class,paymentId);
	}
	

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}


	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}


	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	
	

}
