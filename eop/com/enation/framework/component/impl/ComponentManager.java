package com.enation.framework.component.impl;

import java.util.ArrayList;
import java.util.List;

import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentManager;
import com.enation.framework.component.IComponentStartAble;
import com.enation.framework.component.PluginView;
import com.enation.framework.component.context.ComponentContext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.plugin.IPluginBundle;
 
/**
 * 组件管理
 * @author kingapex
 *2012-5-15上午6:58:38
 */
public class ComponentManager extends BaseSupport implements IComponentManager {
	/**
	 * 先由组件上下文中加载组件列表
	 * 再由数据库中加载组件状态
	 */
	@Override
	public List<ComponentView> list() {
		List<ComponentView> viewList = new ArrayList<ComponentView>();
		List<ComponentView> componentViews = ComponentContext.getComponents();// 加载所有声明的组件
		List<ComponentView> dbList = getDbList();
		
		if (componentViews != null) {
			for (ComponentView view : componentViews) {
				ComponentView componentView = (ComponentView) view.clone();

				if (this.logger.isDebugEnabled()) {
					this.logger.debug("load component["	+ componentView.getName() + "] start ");
				}
				
				
				
				try {
					componentView.setInstall_state(0); // 默认为未安装状态
					componentView.setEnable_state(0); // 默认为未启用					

					this.loadComponentState(componentView, dbList); // 由数据库加载组件信息					

					if (this.logger.isDebugEnabled()) {
						this.logger.debug("load component["	+ componentView.getName() + "] end ");
					}
				} catch (Exception e) {
					this.logger.error("加载组件[" + componentView.getName() + "]出错", e);
					componentView.setEnable_state(2);
					componentView.setError_message(e.getMessage());
				}
				viewList.add(componentView);
			}
		}
		return viewList;
	}

	/**
	 * 用数据库的组件列表加载组件上下文中的组件状态
	 * @param componentView
	 * @param dbList
	 */
	private void loadComponentState(ComponentView componentView, List<ComponentView> dbList) {
		for (ComponentView dbView : dbList) {
			if (dbView.getComponentid().equals(componentView.getComponentid())) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("load component[" + componentView.getName()
							+ "]state->install state:"
							+ dbView.getInstall_state() + ",enable_state:"
							+ dbView.getEnable_state());
				}				
				componentView.setInstall_state(dbView.getInstall_state());
				componentView.setEnable_state(dbView.getEnable_state());
				componentView.setId(dbView.getId());
				
				if (componentView.getInstall_state() != 2) {
					if (dbView.getEnable_state() == 0) {
						// this.stop(dbView.getComponentid() );
					} else if (dbView.getEnable_state() == 1) {
						// this.start(dbView.getComponentid());
					} else {
						componentView.setError_message(dbView.getError_message());
					}
				}
			}
		}
	}
	
	@Override
	public void install(String componentid) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("install component[" + componentid + "]...");
		}
		
		try {
			ComponentView componentView = this.getComponentView(componentid);
			if (componentView != null) {
				componentView.getComponent().install();
				if (!this.isInDb(componentView.getComponentid())) {// 数据库中还不存在，需要先插入一条
					ComponentView temp = (ComponentView) componentView.clone();
					temp.setInstall_state(1);

					this.baseDaoSupport.insert("component", temp);
				} else {
					componentView.setInstall_state(1);
					this.baseDaoSupport.execute("update component set install_state=1 where componentid=?",	componentView.getComponentid());
				}
			}
		} catch (RuntimeException e) {
			this.logger.error("安装组件[" + componentid + "]出错", e);
		}
	}
	
	private boolean isInDb(String componentid) {
		String sql = "select count(0)  from component where componentid=?";
		return this.baseDaoSupport.queryForInt(sql, componentid) > 0;
	}

	@Override
	public void unInstall(String componentid) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("install component[" + componentid + "]...");
		}
		
		ComponentView componentView = this.getComponentView(componentid);
		if (componentView != null) {
			componentView.getComponent().unInstall();
			if (!this.isInDb(componentView.getComponentid())) {
				ComponentView temp = (ComponentView) componentView.clone();
				temp.setInstall_state(0);
				this.baseDaoSupport.insert("component", temp);
			} else {
				this.baseDaoSupport.execute("update component set install_state=0 where componentid=?",	componentView.getComponentid());
			}
		}
	}
	
	private ComponentView getComponentView(String componentid) {
		List<ComponentView> componentList = ComponentContext.getComponents();
		for (ComponentView componentView : componentList) {
			if (componentView.getComponentid().equals(componentid)) {
				return componentView;
			}
		}
		return null;
	}
	/**
	 * 获取组件视图根据组件名称
	 * @param componentName
	 * @return
	 */
	public ComponentView getCmptView(String componentName) {
		List<ComponentView> componentList = ComponentContext.getComponents();
		for (ComponentView componentView : componentList) {
			if (componentView.getName().equals(componentName)) {
				return componentView;
			}
		}
		return null;
	}
	/**
	 * 启用某个组件
	 * 将其插件及挂件启用
	 * @param componentid 组件标识id
	 */
	@Override
	public void start(String componentid) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("start component[" + componentid + "]...");
		}
		
		
		Object object = SpringContextHolder.getBean(componentid);
		if (object instanceof IComponentStartAble) {
			IComponentStartAble startAble = (IComponentStartAble) object;
			startAble.start();
		}
		
		
		ComponentView componentView= this.getComponentView(componentid);
		componentView.setEnable_state(1);
		/**
		 * 启用相应插件
		 */
		List<PluginView> pluginList = componentView.getPluginList();
		for (PluginView pluginView : pluginList) {
			String pluginid = pluginView.getId();
			IPlugin plugin = SpringContextHolder.getBean(pluginid);

			List<String> bundleList = pluginView.getBundleList();
			if (bundleList != null) {
				for (String bundleId : bundleList) {
					IPluginBundle pluginBundle = SpringContextHolder.getBean(bundleId);
					pluginBundle.registerPlugin(plugin);
				}
			}
		}				
				
		
				
		/**
		 * 更新数据库的状态
		 */
		String sql = "update component set enable_state=1 where componentid=?";
		this.baseDaoSupport.execute(sql, componentid);			
		
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("start component[" + componentid + "] complete");
		}		
	}

	/**
	 * 停用某个组件
	 * 将其插件及挂件停用
	 * @param componentid 组件标识id
	 */
	@Override
	public void stop(String componentid) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("stop component[" + componentid + "]...");
		}	
		
		ComponentView componentView= this.getComponentView(componentid);
		componentView.setEnable_state(0);		
		/**
		 * 
		 * 停用相应插件
		 */
		List<PluginView> pluginList = componentView.getPluginList();
		for (PluginView pluginView : pluginList) {
			String pluginid = pluginView.getId();
			IPlugin plugin = SpringContextHolder.getBean(pluginid);
			List<String> bundleList = pluginView.getBundleList();
			for (String bundleId : bundleList) {
				IPluginBundle pluginBundle = SpringContextHolder.getBean(bundleId);
				pluginBundle.unRegisterPlugin(plugin);
			}
		}
				
		 
				
		/**
		 * 更新数据库的状态
		 */
		String sql = "update component set enable_state=0 where componentid=?";
		this.baseDaoSupport.execute(sql, componentid);				
				
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("stop component[" + componentid + "] complete");
		}		
	}
	
	/**
	 * 启动站点组件
	 */
	@Override
	public void startComponents() {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("start components start...");
		}
 
		List<ComponentView> dbList = getDbList();
		for (ComponentView componentView : dbList) {
			if (componentView.getInstall_state() != 2) {
				if (componentView.getEnable_state() == 1) {
					this.start(componentView.getComponentid());
				}
			}
		}
		
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("start components end!");
		}
	}
 
	
	private List<ComponentView> getDbList() {
		String sql = "select * from component ";
		return this.baseDaoSupport.queryForList(sql, ComponentView.class);
	}

}
