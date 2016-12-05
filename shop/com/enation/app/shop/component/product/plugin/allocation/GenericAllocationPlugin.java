package com.enation.app.shop.component.product.plugin.allocation;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.plugin.order.IOrderAllocationItemEvent;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 普通商品配货插件
 * @author kingapex
 *
 */
@Component
public class GenericAllocationPlugin extends AutoRegisterPlugin
		implements IOrderAllocationItemEvent {

	private IGoodsStoreManager goodsStoreManager;
	
	@Override
	public void filterAlloViewItem(Map colValues, ResultSet rs) {
		
		
	}
	/**
	 *获取某货品的各个库房库存情况，以便形成配货输入项 
	 */
	@Override
	public  String getAllocationStoreHtml(OrderItem item) {
		List<Map> storeList  = this.goodsStoreManager.listProductStore(item.getProduct_id()) ; //获取此货品的库存情况
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("item",item);
		freeMarkerPaser.putData("storeList",storeList);
		return freeMarkerPaser.proessPageContent();
	}

	
	
	/***
	 * 响应“订单项”配货事件
	 */
	@Override
	public void onAllocation(AllocationItem allocationItem) {
	
	}
	
	
	@Override
	public String getAllocationViewHtml(OrderItem item) {
		List<Map> storeList = this.goodsStoreManager.listProductAllo(item.getOrder_id(), item.getItem_id()); //获取这个货物项的配货情况
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("item",item);
		freeMarkerPaser.putData("storeList",storeList);
		freeMarkerPaser.setPageName("view_allocation");
		return freeMarkerPaser.proessPageContent();
	}
	
	

	@Override
	public boolean canBeExecute(int catid) {
			return true;
	}



	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}



	


}
