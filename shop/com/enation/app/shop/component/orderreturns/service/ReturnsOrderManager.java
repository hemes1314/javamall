package com.enation.app.shop.component.orderreturns.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.ReturnsOrder;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.DateUtil;

/**
 * 退货管理
 * @author kingapex ;modify by dable
 *
 */
@Component
public class ReturnsOrderManager extends BaseSupport implements IReturnsOrderManager {

	
	private IOrderFlowManager orderFlowManager ;
	private IOrderManager orderManager;
	private IReturnsOrderManager returnsOrderManager;
	
	public IReturnsOrderManager getReturnsOrderManager() {
		return returnsOrderManager;
	}

	public void setReturnsOrderManager(IReturnsOrderManager returnsOrderManager) {
		this.returnsOrderManager = returnsOrderManager;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void add(ReturnsOrder returnsOrder,int orderid,int state,int[] goodids) {
		returnsOrder.setState(ReturnsOrderStatus.APPLY_SUB);
		returnsOrder.setAdd_time(DateUtil.getDateline());
		for (int i = 0; i < goodids.length; i++) {
			this.baseDaoSupport.execute("update order_items set state = ? where order_id = ? and goods_id= ?", state,orderid,goodids[i]);
		}
		this.baseDaoSupport.insert("returns_order", returnsOrder);

	}
	
	public ReturnsOrder get(Integer id) {
		ReturnsOrder order =(ReturnsOrder)baseDaoSupport.queryForObject("select * from returns_order where id=?", ReturnsOrder.class, id);
//		
//		List itemList  = orderManager.listGoodsItems(order.getOrderid());
//		order.setItemList(itemList);
		return order;
	}
	
	public ReturnsOrder getByOrderSn(String ordersn){
		ReturnsOrder order =(ReturnsOrder)baseDaoSupport.queryForObject("select * from returns_order where ordersn=?", ReturnsOrder.class, ordersn);
		return order;
	}

	
	public Page listAll(int pageNo, int pageSize) {
		String sql ="select r.*,m.uname as membername from " + this.getTableName("returns_order") + " r," + this.getTableName("member") + " m where r.memberid=m.member_id   order by r.add_time desc";
		//System.out.println(sql);
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, ReturnsOrder.class);
	}

	
	public List<ReturnsOrder> listMemberOrder() {
		Member member = UserConext.getCurrentMember();
		return this.baseDaoSupport.queryForList("select * from returns_order where memberid =? ", ReturnsOrder.class, member.getMember_id());
	}

	public void updateState(Integer returnOrderId, int state) {
		this.baseDaoSupport.execute("update returns_order set state=? where id=?", state,returnOrderId);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void refuse(Integer return_id,String refuse_reason,int return_state) {
		this.baseDaoSupport.execute("update returns_order set state=?,refuse_reason=? where id=?",return_state,refuse_reason,return_id);
	}
	
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}


	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@Override
	/**
	 * state为3表示已删除的记录
	 */
	public Page pageReturnOrder(int pageNo, int pageSize) {
			Member member = UserConext.getCurrentMember();
			String sql = "select * from returns_order where memberid = ? order by add_time desc";
			Page rpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id());
			return rpage;
	}

	/**
	 * 获得货号字串
	 */
	@Override
	public String getSnByOrderSn(String orderSn) {
		return (String)this.baseDaoSupport.queryForObject("select goodsns from returns_order where ordersn = ?", new StringMapper(), orderSn);
	}

	/**
	 * 更新订单货物状态
	 */
	@Override
	public void updateOrderItemsState(Integer itemsId,int state) {
		this.baseDaoSupport.execute("update order_items set state = ? where item_id = ?", state,itemsId);
	}

	/**
	 *根据id查询价格返回double对象
	 */
	@Override
	public Double queryItemPrice(Integer orderid,Integer state) {
		Double temp=(Double)this.baseDaoSupport.queryForObject("SELECT sum(price) as price FROM order_items  where order_id = ? and state= ?",new DoubleMapper(),orderid,state);
		return temp;
	}

	@Override
	public void updateItemChange(String change_goods_name, int change_goods_id,int itemId) {
		this.baseDaoSupport.execute("update order_items set change_goods_name =?,change_goods_id=? where item_id = ?", change_goods_name,change_goods_id,itemId);
	}

	@Override
	public void updateItemStatusByOrderidAndStatus(int newStatus,
			int prevStatus, int orderid) {
		this.baseDaoSupport.execute("update order_items set state = ? where order_id = ? and state=?", newStatus,orderid,prevStatus);
	}

	@Override
	public int queryOrderidByReturnorderid(int returnorderid) {
		return orderManager.get(returnsOrderManager.get(returnorderid).getOrdersn()).getOrder_id().intValue();
	}

	@Override
	public void updatePriceByItemid(int itemid, double price) {
		this.baseDaoSupport.execute("update order_items set price=? where item_id=?", price,itemid);
	}

	@Override
	public Page listAll(int pageNo, int pageSize, int state) {
		return this.daoSupport.queryForPage("select r.*,m.uname as membername from " + this.getTableName("returns_order") + " r," + this.getTableName("member") + " m where r.memberid=m.member_id and r.state = ?   order by r.add_time desc", pageNo, pageSize, ReturnsOrder.class,state);
	}


	/**
	 * 添加订单日志
	 * @param log
	 */
	public  void addLog(OrderLog log){
		log.setOp_time(System.currentTimeMillis());
		this.baseDaoSupport.insert("order_log", log);
	}
 
	
	
	public Integer getOrderidByReturnid(Integer returnorderid){
		Integer orderid=this.orderManager.get(this.returnsOrderManager.get(returnorderid).getOrdersn()).getOrder_id();
		return orderid;
	}

	@Override
	public void updateItemsState(Integer orderid, int nowstate, int prestate) {
		this.baseDaoSupport.execute("update order_items set state = ?  where order_id =? and state = ? ", nowstate,orderid,prestate);
	}
}
