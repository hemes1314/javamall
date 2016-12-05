package com.enation.app.b2b2c.core.pluin.order;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 店铺购物车插件桩
 * @author kingapex
 * @version 1.0
 * 2015年8月21日下午3:16:21
 */
@Component
public class StoreCartPluginBundle extends AutoRegisterPluginsBundle {

	/**
	 * 激发子订单价格计算事件
	 * @param orderpice
	 * @return
	 */
	public OrderPrice countChildPrice(OrderPrice orderpice){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICountChildOrderPriceEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "storecart.countPrice开始...");
						}
						ICountChildOrderPriceEvent event = (ICountChildOrderPriceEvent) plugin;
						orderpice =event.countChildPrice(orderpice);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " storecart.countPrice结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
		 
 		return orderpice;
	}
	
	
	
	@Override
	public String getName() {
		
		return "多店铺购物车插件桩";
	}
	
}
