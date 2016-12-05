package com.enation.app.b2b2c.component.plugin.store;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.component.plugin.store.IAfterStorePassEvent;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.app.shop.core.plugin.order.IOrderTabShowEvent;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;
/**
 * 店铺插件桩
 * @author LiFenLong
 *
 */
@Component
public class StorePluginBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		return "店铺插件桩";
	}
	
	public void onAfterPass(Store store){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IAfterStorePassEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterPass 开始...");
						}
						IAfterStorePassEvent event = (IAfterStorePassEvent) plugin;
						event.AfterStorePass(store);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterPass 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用店铺插件[店铺通过]事件错误", e);
			throw e;
		}
	}
	public void onAfterApply(Store store){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IAfterStoreApplyEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterPass 开始...");
						}
						IAfterStoreApplyEvent event = (IAfterStoreApplyEvent) plugin;
						event.IAfterStoreApply(store);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterPass 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用店铺插件[申请店铺]事件错误", e);
			throw e;
		}
	}

}
