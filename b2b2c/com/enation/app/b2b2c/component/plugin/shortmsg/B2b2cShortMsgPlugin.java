package com.enation.app.b2b2c.component.plugin.shortmsg;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.IShortMessageEvent;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.component.orderreturns.service.ReturnsOrderStatus;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 网店短消息提醒插件
 * @author Kanon
 *
 */
@Component
public class B2b2cShortMsgPlugin extends AutoRegisterPlugin implements IShortMessageEvent {
	
	private IDaoSupport daoSupport;
	private IPermissionManager permissionManager;
	
	@Override
	public List<ShortMsg> getMessage() {
		if(EopSetting.PRODUCT.equals("b2b2c")){
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
			
			
			//查找发货任务
			//隐藏 chenzhongwei update 
			/*if(haveDepotAdmin){
				ShortMsg msg= this.getShipMessage(adminUser);
				if(msg!=null)
				msgList.add(msg);
			}*/
			
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
	 * 获取退换货消息
	 * @return
	 */
	private ShortMsg getReturnsOrderMessage(){
		String sql  ="select count(0) from es_returns_order where state =? ";		
		int count = this.daoSupport.queryForInt(sql, ReturnsOrderStatus.APPLY_SUB);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/b2b2c/admin/storeOrderReport!returnedList.do?state="+ ReturnsOrderStatus.APPLY_SUB);
			msg.setTitle("退换货申请单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个退换货申请单");
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
			String sql  ="select count(0) from es_order where status=? and disabled=0 AND parent_id is NOT NULL ";		
			count = this.daoSupport.queryForInt(sql, OrderStatus.ORDER_ALLOCATION_YES);
		}else{
			String sql  ="select count(0) from es_order where (status=? or (payment_type='cod' and status=0)) and disabled=0 AND parent_id is NOT NULL ";		
			count = this.daoSupport.queryForInt(sql, OrderStatus.ORDER_PAY_CONFIRM);
		}
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/b2b2c/admin/storeOrder!list.do?order_state=wait_ship");
			msg.setTitle("待发货订单");
			msg.setTarget("ajax");
			msg.setContent("有"+count +"个订单需要您发货");
			return msg;
		}
		return null;
	}
	/**
	 * 获取待确认付款任务消息
	 * @return
	 */
	private ShortMsg getPayConfirmMessage(){
		String sql  ="select count(0) from es_order where  disabled=0 and status=? and payment_type != 'cod'  AND parent_id is NOT NULL ";		
		int count = this.daoSupport.queryForInt(sql, OrderStatus.ORDER_NOT_PAY);
		String sql_1 = "select count(0) from es_order where disabled=0 and status=? and payment_type = 'cod' AND parent_id is NOT NULL ";	
		int count_1 =this.daoSupport.queryForInt(sql_1,OrderStatus.ORDER_ROG);
		if((count+count_1)>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/b2b2c/admin/storeOrder!notPayOrder.do");
			msg.setTitle("待结算订单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count+count_1) +"个新订单需要您结算");
			return msg;
		}
		return null;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}
	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

}
