package com.enation.app.b2b2ccostdown.component;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2ccostdown.component.act.ICostDownActAddEvent;
import com.enation.app.b2b2ccostdown.component.act.ICostDownActDeleteEvent;
import com.enation.app.b2b2ccostdown.component.act.ICostDownActEndEvent;
import com.enation.app.b2b2ccostdown.component.act.ICostDownActStartEvent;
import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;
/**
 * 抢购插件桩
 * @author fenlongli
 *
 */
@Component
public class CostDownPluginBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		return "直降插件桩";
	}
	/**
	 * 激发抢购活动删除事件
	 * @param CostDownActive
	 */
	public void onCostDownDelete(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ICostDownActDeleteEvent) {
					ICostDownActDeleteEvent event = (ICostDownActDeleteEvent) plugin;
					event.onDeleteCostDownAct(act_id);
				}
			}
		}
	}
	/**
	 * 激发活动启动事件
	 * @param CostDownActive
	 */
	public void onCostDownStart(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ICostDownActStartEvent) {
					ICostDownActStartEvent event = (ICostDownActStartEvent) plugin;
					event.onCostDownStart(act_id);
				}
			}
		}
	}
	/**
	 * 激发活动关闭事件
	 * @param CostDownActive
	 */
	public void onCostDownEnd(Integer act_id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ICostDownActEndEvent) {
					ICostDownActEndEvent event = (ICostDownActEndEvent) plugin;
					event.onEndCostDownEnd(act_id);
				}
			}
		}
	}
	/**
	 * 激发活动添加事件
	 * @param CostDownActive
	 */
	public void onCostDownAdd(CostDownActive CostDownActive) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof ICostDownActAddEvent) {
					ICostDownActAddEvent event = (ICostDownActAddEvent) plugin;
					event.onAddCostDownAct(CostDownActive);
				}
			}
		}
	}
}
