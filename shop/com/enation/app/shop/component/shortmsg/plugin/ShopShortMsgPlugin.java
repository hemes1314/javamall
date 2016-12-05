package com.enation.app.shop.component.shortmsg.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.IShortMessageEvent;
import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.component.orderreturns.service.ReturnsOrderStatus;
import com.enation.app.shop.core.model.Allocation;
import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.service.IDepotUserManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 网店短消息提醒插件
 * @author kingapex
 *
 */
@Component
public class ShopShortMsgPlugin extends AutoRegisterPlugin implements IShortMessageEvent{
	
	private IShortMsgManager shortMsgManager ;
	private IDepotUserManager depotUserManager;
	private IRoleManager roleManager ;
	private IAdminUserManager adminUserManager;
	private IPermissionManager permissionManager;
	private IDaoSupport daoSupport;
	private IDaoSupport baseDaoSupport;
	
	private IDBRouter baseDBRouter; 
	
	@Override
	public List<ShortMsg> getMessage() {
		if(EopSetting.PRODUCT.equals("b2c")){
			List<ShortMsg> msgList  = new ArrayList<ShortMsg>();
			AdminUser adminUser  = UserConext.getCurrentAdminUser();
			
			boolean haveDepotAdmin = this.permissionManager.checkHaveAuth( PermissionConfig.getAuthId("depot_admin")  ); //库存管理 权限
			boolean haveFinance = this.permissionManager.checkHaveAuth( PermissionConfig.getAuthId("finance")  ); 	//财务 权限
			boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));	 //订单管理员权限
			boolean haveCustomerService = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("customer_service"));//客服权限
			
			//查找退换货任务
			if(haveOrder||haveCustomerService){
				ShortMsg msg= this.getReturnsOrderMessage();
				if(msg!=null){
					msgList.add(msg);
				}
			}
			
			//查找咨询评论任务,查找货到付款订单确认任务
			if(haveCustomerService){
				ShortMsg discuss= this.getDiscussMessage();
				ShortMsg ask= this.getAskMessage();
				ShortMsg confirm = this.getOrderConfirm();
				if(discuss!=null){
					msgList.add(discuss);
				}
				if(ask!=null){
					msgList.add(ask);
				}
				if(confirm!=null){
					//msgList.add(confirm);
				}
			}
			
			
			//查找发货任务
			if(haveDepotAdmin){
				ShortMsg msg= this.getShipMessage(adminUser);
				if(msg!=null)
				msgList.add(msg);
			}
			
			//查找待确认付款任务消息 
			if(haveFinance){
				ShortMsg msg= this.getPayConfirmMessage();
				if(msg!=null)
				msgList.add(msg);
			}
			
			return msgList;
		}
		return null;
	}

	/**
	 * 获取订单确认任务
	 * @return
	 */
	private ShortMsg getOrderConfirm(){
		String sql = "select count(0) from order where status=? and   payment_type = 'cod'";
		int count = this.baseDaoSupport.queryForInt(sql, OrderStatus.ORDER_NOT_PAY);
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order!listbyship.do?state="+ OrderStatus.ORDER_NOT_PAY+"&shipping_id=2");
			msg.setTitle("有"+count +"个订单需要完成确认订单任务");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取待确认付款任务消息
	 * @return
	 */
	private ShortMsg getPayConfirmMessage(){
		String sql  ="select count(0) from order where  disabled=0 and status=? and payment_type != 'cod'  ";		
		int count = this.baseDaoSupport.queryForInt(sql, OrderStatus.ORDER_NOT_PAY);
		String sql_1 = "select count(0) from order where disabled=0 and status=? and payment_type = 'cod' ";	
		int count_1 =this.baseDaoSupport.queryForInt(sql_1,OrderStatus.ORDER_ROG);
		if((count+count_1)>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order!notPayOrder.do");
			msg.setTitle("待结算订单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count+count_1) +"个新订单需要您结算");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取发货任务消息 
	 * @return
	 */
	private ShortMsg getShipMessage(AdminUser adminUser){
		int count=0;
		if(adminUser.getFounder()==0){
			DepotUser depotUser  = (DepotUser)adminUser;
			String sql  ="select count(0) from order where status=? and depotid=? and disabled=0";		
			count = this.baseDaoSupport.queryForInt(sql, OrderStatus.ORDER_ALLOCATION_YES,depotUser.getDepotid());
		}else{
			String sql  ="select count(0) from order where (status=? or (payment_type='cod' and status=0)) and disabled=0 ";		
			count = this.baseDaoSupport.queryForInt(sql, OrderStatus.ORDER_PAY_CONFIRM);
		}
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order!notShipOrder.do");
			msg.setTitle("待发货订单");
			msg.setTarget("ajax");
			msg.setContent("有"+count +"个订单需要您发货");
			return msg;
		}
		return null;
	}
	
	
	
	
	/**
	 * 获取进货任务消息
	 * @return
	 */
	private ShortMsg getStockMessage(AdminUser adminUser){
		
		int count=0;
		if(adminUser.getFounder()==0){
			DepotUser depotUser  = (DepotUser)adminUser;
			//String sql  ="select count(0) from " + baseDBRouter.getTableName("goods_depot") + " gd left join " + baseDBRouter.getTableName("goods") + " g on gd.goodsid=g.goods_id where iscmpl=0 and depotid=? and g.disabled=0";
			String sql="select count(0) from "+ baseDBRouter.getTableName("goods")+" where goods_id in (select goodsid from " + baseDBRouter.getTableName("goods_depot") + "   where iscmpl=0 and depotid=?) ";
			count = this.daoSupport.queryForInt(sql, depotUser.getDepotid());
		}else{
			
			String sql="select count(0) from "+ baseDBRouter.getTableName("goods")+" where goods_id in (select goodsid from " + baseDBRouter.getTableName("goods_depot") + "   where iscmpl=0 ) ";
			count = this.daoSupport.queryForInt(sql);
		}
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/goods!list.do?optype=stock");
			msg.setTitle("进货");
			msg.setContent("有"+count +"个商品需要您完成进货任务");
		 
			return msg;
		}
		
		
		return null;
	}
	
	
	/**
	 * 获取配货任务下达消息 
	 * @return
	 */
	private ShortMsg getAlloMessage(){
		String sql  ="select count(0) from order where status=? ";		
		int count = this.baseDaoSupport.queryForInt(sql, OrderStatus.ORDER_PAY_CONFIRM);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order!list.do?state="+ OrderStatus.ORDER_PAY_CONFIRM);
			msg.setTitle("有"+count +"个订单需要您下达配货任务");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取退换货消息
	 * @return
	 */
	private ShortMsg getReturnsOrderMessage(){
		String sql  ="select count(0) from returns_order where state =? ";		
		int count = this.baseDaoSupport.queryForInt(sql, ReturnsOrderStatus.APPLY_SUB);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/returnOrder!returnsApplyList.do?state="+ ReturnsOrderStatus.APPLY_SUB);
			msg.setTitle("退换货申请单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个退换货申请单");
			return msg;
		}
		return null;
	}
	
	
	/**
	 * 获取评论
	 * @return
	 */
	private ShortMsg getDiscussMessage(){
		String sql  ="select count(0) from member_comment where type=1 and status=0 ";		
		int count = this.baseDaoSupport.queryForInt(sql);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/comments!msgList.do?type=1&status=0");
			msg.setTitle("评论列表");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个评论需要您处理。");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取咨询
	 * @return
	 */
	private ShortMsg getAskMessage(){
		String sql  ="select count(0) from member_comment where type=2 and status=0 ";		
		int count = this.baseDaoSupport.queryForInt(sql);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/comments!list.do?type=2");
			msg.setTitle("咨询列表");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个咨询需要您处理。");
			return msg;
		}
		return null;
	}
	
	
	/**
	 * 获取配货任务消息 
	 * @param depotid
	 * @return
	 */
	private ShortMsg getDepotAlloMessage(AdminUser adminUser  ){
		String sql;
		int count=0;
		if(adminUser.getFounder()==0){
			DepotUser depotUser  = (DepotUser)adminUser;		
			sql  ="select count(0) from allocation_item where iscmpl=0 and depotid=?";
			count = this.baseDaoSupport.queryForInt(sql, depotUser.getDepotid());
		}else{
			sql  ="select count(0) from allocation_item where iscmpl=0 ";
			count = this.baseDaoSupport.queryForInt(sql);
		}
		
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/orderReport!allocationList.do");
			msg.setTitle("有"+count +"个商品需要您完成配货任务");
			return msg;
		}
		return null;
	}
	
	
	
	/**
	 * 响应配货事件
	 */ 
	 
	public void onAllocation(Allocation allocation) { 
	/*	int roleid  = this.roleManager .getRoleConfigId("allocation"); //获取配货员角色的id
		List<DepotUser > userList  = depotUserManager.listByRoleId(roleid); //获取所有配货员的列表
				
		List<AllocationItem> itemList =allocation.getItemList();
				
		
		for(DepotUser user: userList){ //找到这个部门的用户
			 	int itemCount = this.findItemCount(user.getDepotid(), itemList);
			 	if(itemCount >0 )  {
			 		
			 		String title = "有"+itemCount+"商品配货";
			 		String content= "有"+itemCount+"商品需要您配货，请尽快配货";
			 		String url = "/shop/admin/orderReport!allocationList.do"; 
			 				
					ShortMsg msg = new ShortMsg();
					msg.setTitle(title) ;
					msg.setContent(content);
					msg.setUrl(url);
					msg.setUserid(user.getUserid());
					msg.setTarget("ajax");
					this.shortMsgManager.add(msg);
					
			 	} 
		}
		
		
		 
		roleid  = this.roleManager .getRoleConfigId("ship"); //获取发货员角色的id
		userList  = depotUserManager.listByRoleId(allocation.getShipDepotId(),roleid); //获取订单的发货员id
		
		for(DepotUser user:userList){
			String title = "有一个新订单需要发货";
	 		String content= "有一个新订单需要您发货，请尽快发货";
	 		String url = "/shop/admin/order!list.do"; 
	 				
			ShortMsg msg = new ShortMsg();
			msg.setTitle(title) ;
			msg.setContent(content);
			msg.setUrl(url);
			msg.setUserid(user.getUserid());
			msg.setTarget("ajax");
			this.shortMsgManager.add(msg);
		}
		*/
	}
	
	
	/**
	 * 查找某个部门的配货项
	 * @param depotid
	 * @param itemList
	 * @return
	 */
	private int findItemCount(int depotid,List<AllocationItem> itemList){
		int count = 0;
		for(AllocationItem item : itemList){ //循环所有配货项，找到其配货的部门
			if (depotid  == item.getDepotid() ){
				count ++;
			}
			
		}		
		return count;
		
	}
	
	
	 

	public IShortMsgManager getShortMsgManager() {
		return shortMsgManager;
	}

	public void setShortMsgManager(IShortMsgManager shortMsgManager) {
		this.shortMsgManager = shortMsgManager;
	}

	public IDepotUserManager getDepotUserManager() {
		return depotUserManager;
	}

	public void setDepotUserManager(IDepotUserManager depotUserManager) {
		this.depotUserManager = depotUserManager;
	}


	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

 

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}

}
