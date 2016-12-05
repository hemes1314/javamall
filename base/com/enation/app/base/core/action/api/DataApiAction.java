package com.enation.app.base.core.action.api;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.action.WWAction;

@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("data")
public class DataApiAction extends WWAction {
	
	private String tb;
	public String export(){
		
		try {
			String[] tables={tb};
			this.json=DBSolutionFactory.dbExport(tables, false, "");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("导出失败");
		}
		
		
		return this.JSON_MESSAGE;
	}
	public String getTb() {
		return tb;
	}
	public void setTb(String tb) {
		this.tb = tb;
	}
	
	
}
