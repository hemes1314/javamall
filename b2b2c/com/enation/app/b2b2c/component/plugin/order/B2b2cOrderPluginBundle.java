package com.enation.app.b2b2c.component.plugin.order;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * b2b2c订单插件桩
 * @author FengXingLong
 * 2015-07-21
 */
@Component
public class B2b2cOrderPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "b2b2c订单插件桩";
	}
	
	/**
	 * 当子订单的 子项保存时
	 * @param order
	 * @param item
	 */
	public void onChildItemSave(Order order,OrderItem item){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IChildItemSaveEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onChildItemSave 开始...");
						}
						IChildItemSaveEvent event = (IChildItemSaveEvent) plugin;
						event.onChildItemSave(order, item);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onChildItemSave 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用b2b2c订单插件[子订单保存]事件错误", e);
			throw e;
		}
	}
	
	/**
	 * 当子订单创建完成后
	 * @param order
	 * @param itemList
	 * @param sessionid
	 */
	public void onAfterOrderChildCreate(Order order ,List<CartItem>   itemList,String sessionid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IAfterOrderChildCreateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterOrderChildCreate 开始...");
						}
						IAfterOrderChildCreateEvent event = (IAfterOrderChildCreateEvent) plugin;
						event.onAfterOrderChildCreate(order, itemList, sessionid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterOrderChildCreate 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用b2b2c订单插件[子订单创建后]事件错误", e);
			throw e;
		}
	}

}
