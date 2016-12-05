package com.enation.app.advbuy.component.plugin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.advbuy.component.plugin.act.IAdvBuyActAddEvent;
import com.enation.app.advbuy.component.plugin.act.IAdvBuyActDeleteEvent;
import com.enation.app.advbuy.component.plugin.act.IAdvBuyActEndEvent;
import com.enation.app.advbuy.component.plugin.act.IAdvBuyActStartEvent;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;
/**
 * 预售插件桩
 * @author fenlongli
 *
 */
@Component
public class AdvbuyPluginBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "预售插件桩";
	}
	/**
	 * 激发预售活动删除事件
	 * @param AdvBuyActive
	 */
	public void onAdvBuyDelete(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IAdvBuyActDeleteEvent) {
					IAdvBuyActDeleteEvent event = (IAdvBuyActDeleteEvent) plugin;
					event.onDeleteAdvBuyAct(act_id);
				}
			}
		}
	}
	/**
	 * 激发预售活动启动事件
	 * @param AdvBuyActive
	 */
	public void onAdvBuyStart(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IAdvBuyActStartEvent) {
					IAdvBuyActStartEvent event = (IAdvBuyActStartEvent) plugin;
					event.onAdvBuyStart(act_id);
				}
			}
		}
	}
	/**
	 * 激发预售活动关闭事件
	 * @param AdvBuyActive
	 */
	public void onAdvBuyEnd(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IAdvBuyActEndEvent) {
					IAdvBuyActEndEvent event = (IAdvBuyActEndEvent) plugin;
					event.onEndAdvBuyEnd(act_id);
				}
			}
		}
	}
	/**
	 * 激发预售活动添加事件
	 * @param AdvBuyActive
	 */
	public void onAdvBuyAdd(AdvBuyActive advBuyActive) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IAdvBuyActAddEvent) {
					IAdvBuyActAddEvent event = (IAdvBuyActAddEvent) plugin;
					event.onAddAdvBuyAct(advBuyActive);
				}
			}
		}
	}
}
