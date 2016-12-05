package com.enation.app.shop.component.ordercore.plugin.shiplog;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.StoreLog;
import com.enation.app.shop.core.plugin.order.IOrderShipEvent;
import com.enation.app.shop.core.service.IStoreLogManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 订单发货库存日志插件
 * @author kingapex
 * @date 2011-11-6 下午4:41:42 
 * @version V1.0
 */
@Component
public class ShipStoreLogPlugin extends AutoRegisterPlugin implements
		IOrderShipEvent {

	private IStoreLogManager storeLogManager;
	private IAdminUserManager adminUserManager;
	 

	@Override
	public void itemShip(Order order,DeliveryItem deliveryItem,
			AllocationItem allocationItem) {
		String other  = allocationItem.getOther();
		int depotid  = allocationItem.getDepotid(); //发货仓库
		int num = allocationItem.getNum(); //配货量
		int goodsid  = allocationItem.getGoodsid(); //商品id
		
		AdminUser adminUser = UserConext.getCurrentAdminUser();		
		DepotUser depotUser = (DepotUser)adminUser;
		StoreLog storeLog = new StoreLog();
		storeLog.setGoodsid(goodsid);
//		storeLog.setGoodsname(goods.get("name").toString());
		storeLog.setDepot_type(1);
		storeLog.setOp_type(2);
		storeLog.setDepotid(depotid);
		storeLog.setDateline(DateUtil.getDateline());
		storeLog.setNum(num);
		storeLog.setUserid(adminUser.getUserid());
		storeLog.setUsername(adminUser.getUsername());
		storeLogManager.add(storeLog);

	}

	@Override
	public void ship(Delivery delivery, List<DeliveryItem> itemList) {
	 

	}


	@Override
	public boolean canBeExecute(int catid) {
		 
		return true;
	}
 

	public IStoreLogManager getStoreLogManager() {
		return storeLogManager;
	}

	public void setStoreLogManager(IStoreLogManager storeLogManager) {
		this.storeLogManager = storeLogManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

}
