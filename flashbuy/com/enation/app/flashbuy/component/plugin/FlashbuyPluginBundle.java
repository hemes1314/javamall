package com.enation.app.flashbuy.component.plugin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActAddEvent;
import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActDeleteEvent;
import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActEndEvent;
import com.enation.app.flashbuy.component.plugin.act.IFlashBuyActStartEvent;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;
/**
 * 抢购插件桩
 * @author fenlongli
 *
 */
@Component
public class FlashbuyPluginBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "抢购插件桩";
	}
	/**
	 * 激发抢购活动删除事件
	 * @param FlashBuyActive
	 */
	public void onFlashBuyDelete(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IFlashBuyActDeleteEvent) {
					IFlashBuyActDeleteEvent event = (IFlashBuyActDeleteEvent) plugin;
					event.onDeleteFlashBuyAct(act_id);
				}
			}
		}
	}
	/**
	 * 激发活动启动事件
	 * @param FlashBuyActive
	 */
	public void onFlashBuyStart(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IFlashBuyActStartEvent) {
					IFlashBuyActStartEvent event = (IFlashBuyActStartEvent) plugin;
					event.onFlashBuyStart(act_id);
				}
			}
		}
	}
	/**
	 * 激发活动关闭事件
	 * @param FlashBuyActive
	 */
	public void onFlashBuyEnd(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IFlashBuyActEndEvent) {
					IFlashBuyActEndEvent event = (IFlashBuyActEndEvent) plugin;
					event.onEndFlashBuyEnd(act_id);
				}
			}
		}
	}
	/**
	 * 激发活动添加事件
	 * @param FlashBuyActive
	 */
	public void onFlashBuyAdd(FlashBuyActive flashBuyActive) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IFlashBuyActAddEvent) {
					IFlashBuyActAddEvent event = (IFlashBuyActAddEvent) plugin;
					event.onAddFlashBuyAct(flashBuyActive);
				}
			}
		}
	}
}
