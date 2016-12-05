/**
 * 
 */
package com.enation.app.base.core.action;

import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.base.core.service.IExampleDataCleanManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 
 * 示例数据清除action
 * @author kingapex
 *2015-6-2
 */

@ParentPackage("eop_default")
@Namespace("/core/admin")
@Results({
	@Result(name="input", type="freemarker", location="/core/admin/data/clean.html")
})
public class ExampleDataCleanAction extends WWAction {
	
	
	private IExampleDataCleanManager exampleDataCleanManager;
	private String[] moudels;
	public String execute(){
		
		return this.INPUT;
	}
	
	public String clean(){
		
		
		
		try {
			
			if(EopSetting.IS_DEMO_SITE){
				this.showErrorJson(EopSetting.DEMO_SITE_TIP);
				return this.JSON_MESSAGE;
			}
			
			
			
			this.exampleDataCleanManager.clean(moudels);
			this.showSuccessJson("清除成功");
		} catch (Exception e) {
			this.logger.error("清除失败", e);
			this.showErrorJson("清除失败");
		}
		return this.JSON_MESSAGE;
	}

	public IExampleDataCleanManager getExampleDataCleanManager() {
		return exampleDataCleanManager;
	}

	public void setExampleDataCleanManager(
			IExampleDataCleanManager exampleDataCleanManager) {
		this.exampleDataCleanManager = exampleDataCleanManager;
	}

	public String[] getMoudels() {
		return moudels;
	}

	public void setMoudels(String[] moudels) {
		this.moudels = moudels;
	}
	
	
	
}
