package com.enation.app.shop.component.spec.plugin.order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.AllocationItem;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.plugin.order.IOrderAllocationItemEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;


/**
 * 规格式商品配货插件
 * @author kingapex
 *2012-3-26下午5:56:05
 */
@Component
public class GoodsSpecAllocationPlugin extends AutoRegisterPlugin implements
		IOrderAllocationItemEvent {
	
	private IOrderAllocationItemEvent genericAllocationPlugin;

	@Override
	public String getAllocationStoreHtml(OrderItem item) {
		
		return genericAllocationPlugin.getAllocationStoreHtml(item);
	}

	@Override
	public String getAllocationViewHtml(OrderItem item) {
		
		return genericAllocationPlugin.getAllocationViewHtml(item);
	}

	@Override
	public void onAllocation(AllocationItem allocationItem) {
		genericAllocationPlugin.onAllocation(allocationItem);

	}

	@Override
	public void filterAlloViewItem(Map colValues, ResultSet rs)
			throws SQLException {
		String addon =  rs.getString("addon");
		
		if(!StringUtil.isEmpty(addon)){
			JSONArray specArray=	JSONArray.fromObject(addon);
			List<Map> specList = (List) JSONArray.toCollection(specArray,Map.class);
			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
			freeMarkerPaser.setClz(this.getClass());
			freeMarkerPaser.putData("specList",specList);
			freeMarkerPaser.setPageName("order_item_spec");
			String html = freeMarkerPaser.proessPageContent(); 
			colValues.put("other", html); //扩展后台订单详细处
			 
			
		}

	}

	@Override
	public boolean canBeExecute(int catid) {
	 
		return true;
	}

	
	
	
	
	 
	public IOrderAllocationItemEvent getGenericAllocationPlugin() {
		return genericAllocationPlugin;
	}

	public void setGenericAllocationPlugin(
			IOrderAllocationItemEvent genericAllocationPlugin) {
		this.genericAllocationPlugin = genericAllocationPlugin;
	}
	
	
}
