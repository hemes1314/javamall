package com.enation.framework.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;

/**
 * 自动注册插件桩
 * 
 * @author apexking
 * 
 */
public abstract class AutoRegisterPluginsBundle implements IPluginBundle {
	protected static final Log loger = LogFactory.getLog(AutoRegisterPluginsBundle.class);

	private List<IPlugin> plugins;

	private Map<String, List<IPlugin>> saasPlugins;

	public synchronized void registerPlugin(IPlugin plugin) {
		 
		this.registerPlugin1(plugin);
	}

	private void registerPlugin1(IPlugin plugin) {
		if (plugins == null) {
			plugins = new ArrayList<IPlugin>();
		}

		if (!plugins.contains(plugin)) {
			plugins.add(plugin);
		}

		if (loger.isDebugEnabled()) {
			loger.debug("为插件桩" + getName() + "注册插件：" + plugin.getClass());
		}
	}

 

	@Override
	public synchronized void unRegisterPlugin(IPlugin _plugin) {
			if (plugins != null) {
				plugins.remove(_plugin);
			}
			
	}

	/**
	 * 获取此插件列表
	 * 
	 * @return
	 */
	public synchronized List<IPlugin> getPlugins() {
		return plugins;
	}

 
	abstract public String getName();

}
