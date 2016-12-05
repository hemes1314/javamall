package com.enation.app.base.core.action;

import com.enation.framework.action.WWAction;
import com.enation.framework.resource.ResourceStateManager;

/**
 * 资源状态action
 * @author kingapex
 *2012-3-16下午9:20:36
 */
public class ResourceStateAction extends WWAction {

	private boolean haveNewDisplaoy;
	public String execute() {
		haveNewDisplaoy = ResourceStateManager.getHaveNewDisploy();
		return this.INPUT;		
	}

	
	public String save(){
		ResourceStateManager.setDisplayState(1);
		this.showSuccessJson("更新成功");
		return this.JSON_MESSAGE;
	}


	public boolean isHaveNewDisplaoy() {
		return haveNewDisplaoy;
	}


	public void setHaveNewDisplaoy(boolean haveNewDisplaoy) {
		this.haveNewDisplaoy = haveNewDisplaoy;
	}
	

}
