package com.enation.app.base.core.service.solution.impl;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.framework.component.IComponentManager;


/**
 * 组件安装器
 * @author kingapex
 *
 */
public class ComponentInstaller implements IInstaller {

	private IComponentManager componentManager;

	@Override
	public void install(String productId, Node fragment) {
		NodeList componentList = fragment.getChildNodes();
		int length = componentList.getLength();

		for (int i = 0; i < length; i++) {
			Node node = componentList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element componentEl = (Element) node;
				String componentid = componentEl.getAttribute("id");
				componentManager.install(componentid);
				componentManager.start(componentid);
			}
		}
	}

	public IComponentManager getComponentManager() {
		return componentManager;
	}

	public void setComponentManager(IComponentManager componentManager) {
		this.componentManager = componentManager;
	}

}
