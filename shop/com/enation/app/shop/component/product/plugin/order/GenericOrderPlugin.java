package com.enation.app.shop.component.product.plugin.order;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.StoreLog;
import com.enation.app.shop.core.plugin.order.IOrderCanelEvent;
import com.enation.app.shop.core.plugin.order.IOrderChangeDepotEvent;
import com.enation.app.shop.core.plugin.order.IOrderItemSaveEvent;
import com.enation.app.shop.core.plugin.order.IOrderShipEvent;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IProductStoreManager;
import com.enation.app.shop.core.service.IStoreLogManager;
import com.enation.app.shop.core.service.StoreLogType;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 普通商品订单插件
 * @author kingapex
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class GenericOrderPlugin extends AutoRegisterPlugin implements IOrderShipEvent,IOrderCanelEvent,IOrderItemSaveEvent,IOrderChangeDepotEvent {
 
	private IDaoSupport baseDaoSupport;
	private IDaoSupport daoSupport;
	private IProductStoreManager productStoreManager;
	private IOrderManager orderManager;
	private IStoreLogManager storeLogManager;
	private IDepotManager depotManager;
	private IAdminUserManager adminUserManager;
	

	/**
	 * 响应订单项保存处理
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onItemSave(Order order,OrderItem item) {
		//更新商品可用库存
		this.productStoreManager.decreaseEnable(item.getGoods_id(), item.getProduct_id(), order.getDepotid(), item.getNum());
		//记录库存日志
		StoreLog storeLog = new StoreLog();
		storeLog.setDateline(  com.enation.framework.util.DateUtil.getDateline());
		storeLog.setDepot_type(0);
		storeLog.setDepotid(order.getDepotid());
		storeLog.setGoodsid(item.getGoods_id());
		storeLog.setGoodsname( item.getName() );
		storeLog.setNum(0);
		storeLog.setEnable_store(0-item.getNum());
		storeLog.setOp_type(StoreLogType.create_order.getType());  //记录为创建订单减少可用库存
		storeLog.setProductid(item.getProduct_id());
		storeLog.setUserid(0L);
		storeLog.setRemark("创建订单["+order.getSn()+"]，减少可用库存");
		storeLog.setUsername("系统");
		this.storeLogManager.add(storeLog);
		
	}
	
	
	/**
	 * 订单发货，减少库存
	 */
	@Override
	public void itemShip(Order order,DeliveryItem deliveryItem,
			AllocationItem allocationItem) {
	 
		int depotid  = order.getDepotid(); //配货仓库
		int num = deliveryItem.getNum(); //配货量
		int goodsid  = deliveryItem.getGoods_id(); //商品id
		int productid  = deliveryItem.getProduct_id();  //货品id
		String name = deliveryItem.getName();
		
		//记录库存日志
		StoreLog storeLog = new StoreLog();
		storeLog.setDateline( DateUtil.getDateline());
		storeLog.setDepot_type(0);
		storeLog.setDepotid(depotid);
		storeLog.setGoodsid(goodsid);
		storeLog.setGoodsname(name);
		storeLog.setNum(0-deliveryItem.getNum());
		storeLog.setOp_type(StoreLogType.ship.getType());   
		storeLog.setProductid(productid);
		storeLog.setUserid(0L);
		storeLog.setUsername("系统");
		storeLog.setRemark("订单["+order.getSn()+"]发货，减少库存");
		this.storeLogManager.add(storeLog);
		
		//更新库存 
		this.productStoreManager.decreaseStroe(goodsid, productid, depotid, num);
	}


	
	/**
	 * 取消订单还原库存
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void canel(Order order) {
		List<Order> orders = null;
		if (order.getParent_id() == null) {
			orders = this.baseDaoSupport.queryForList("select * from order where parent_id=?", Order.class, order.getOrder_id());
		} else {
			orders = Arrays.asList(order);
		}
		List<OrderItem > itemList = null;
		for (Order o : orders) {
			itemList = this.orderManager.listGoodsItems(o.getOrder_id());
			for (OrderItem orderItem : itemList) {
				int goodsid  = orderItem.getGoods_id();
				int productid  = orderItem.getProduct_id();
				int num = orderItem.getNum();
				int depotid = order.getDepotid();
				String name = orderItem.getName();
				// 记录库存日志
				StoreLog storeLog = new StoreLog();
				storeLog.setDateline(DateUtil.getDateline());
				storeLog.setDepot_type(0);
				storeLog.setDepotid(depotid);
				storeLog.setGoodsid(goodsid);
				storeLog.setGoodsname(name);
				storeLog.setNum(0);
				storeLog.setEnable_store(num);
				storeLog.setRemark("取消订单["+order.getSn()+"],增加可用库存");
				storeLog.setOp_type(StoreLogType.cancel_order.getType());  
				storeLog.setProductid(productid);
				storeLog.setUserid(0L);
				storeLog.setUsername("系统");
				this.storeLogManager.add(storeLog);
				// 还原库存
				this.productStoreManager.increaseEnable(goodsid, productid, depotid, num);
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void chaneDepot(Order order, int newdepotid, List<OrderItem> itemList) {
		Depot depot = this.depotManager.get(newdepotid);
		for (OrderItem item : itemList) {
			int goodsid  = item.getGoods_id();
			int num = item.getNum();
			int store = this.productStoreManager.getEnableStroe(goodsid, newdepotid);
			if(store<num){ 
				
				throw new RuntimeException("["+item.getName()+"]库存不足，请求库存["+num+"]在["+depot.getName()+"]中不足，可用库存为["+store+"]");
			}
			int  olddepotid = order.getDepotid();
			this.productStoreManager.decreaseEnable(goodsid, item.getProduct_id(), newdepotid, num); //减少新库可用库存
			this.productStoreManager.increaseEnable(goodsid,  item.getProduct_id(), olddepotid, num);//增加原仓库可用库存
			
			long userid=0;
			String username = "系统";
			
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			if(adminUser!=null){
				userid= adminUser.getUserid();
				username= adminUser.getUsername();
			}
			
			
			//记录库存日志
			//原仓库
			StoreLog storeLog = new StoreLog();
			storeLog.setDateline( DateUtil.getDateline());
			storeLog.setDepot_type(0);
			storeLog.setDepotid(olddepotid);
			storeLog.setGoodsid(goodsid);
			storeLog.setGoodsname(item.getName());
			storeLog.setNum(0);
			storeLog.setEnable_store(num);
			storeLog.setRemark("订单["+order.getSn()+"]仓库修改为["+depot.getName()+"],增加可用库存");
			storeLog.setOp_type(StoreLogType.change_depot.getType());  
			storeLog.setProductid(item.getProduct_id());
			storeLog.setUserid(userid);
			storeLog.setUsername(username);
			this.storeLogManager.add(storeLog);
			
			//新仓库
			storeLog.setDepotid(newdepotid);
			storeLog.setEnable_store(0-num);
			storeLog.setRemark("订单["+order.getSn()+"]仓库修改为["+depot.getName()+"],减少可用库存");
			this.storeLogManager.add(storeLog);
		}
	}
	
	
	
	@Override
	public void ship(Delivery delivery, List<DeliveryItem> itemList) {
	}
	
	@Override
	public boolean canBeExecute(int catid) {
			return true;
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	public IProductStoreManager getProductStoreManager() {
		return productStoreManager;
	}

	public void setProductStoreManager(IProductStoreManager productStoreManager) {
		this.productStoreManager = productStoreManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IStoreLogManager getStoreLogManager() {
		return storeLogManager;
	}

	public void setStoreLogManager(IStoreLogManager storeLogManager) {
		this.storeLogManager = storeLogManager;
	}


	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}


	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


	public IDepotManager getDepotManager() {
		return depotManager;
	}


	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	
	
	
	
}
