package com.enation.app.secbuy.component.plugin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.secbuy.component.plugin.act.ISecBuyActAddEvent;
import com.enation.app.secbuy.component.plugin.act.ISecBuyActDeleteEvent;
import com.enation.app.secbuy.component.plugin.act.ISecBuyActEndEvent;
import com.enation.app.secbuy.component.plugin.act.ISecBuyActStartEvent;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;
/**
 * 秒拍插件桩
 * @author fenlongli
 *
 */
@Component
public class SecbuyPluginBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "秒拍插件桩";
	}
	/**
	 * 激发秒拍活动删除事件
	 * @param SecBuyActive
	 */
	public void onSecBuyDelete(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ISecBuyActDeleteEvent) {
					ISecBuyActDeleteEvent event = (ISecBuyActDeleteEvent) plugin;
					event.onDeleteSecBuyAct(act_id);
				}
			}
		}
	}
	/**
	 * 激发秒拍活动启动事件
	 * @param SecBuyActive
	 */
	public void onSecBuyStart(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ISecBuyActStartEvent) {
					ISecBuyActStartEvent event = (ISecBuyActStartEvent) plugin;
					event.onSecBuyStart(act_id);
				}
			}
		}
	}
	/**
	 * 激发秒拍活动关闭事件
	 * @param SecBuyActive
	 */
	public void onSecBuyEnd(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ISecBuyActEndEvent) {
					ISecBuyActEndEvent event = (ISecBuyActEndEvent) plugin;
					event.onEndSecBuyEnd(act_id);
				}
			}
		}
	}
	/**
	 * 激发秒拍活动添加事件
	 * @param SecBuyActive
	 */
	public void onSecBuyAdd(SecBuyActive secBuyActive) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ISecBuyActAddEvent) {
					ISecBuyActAddEvent event = (ISecBuyActAddEvent) plugin;
					event.onAddSecBuyAct(secBuyActive);
				}
			}
		}
	}
}
