/**
 * 
 */
package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * 检测某组件是否存在
 * @author kingapex
 *2015-5-7
 */
@ParentPackage("eop_default")
@Namespace("/core/admin")
public class CheckComponentAction extends WWAction {
 
	private String id;
	public String execute(){
		try {
			
			Object obj = SpringContextHolder.getBean(id);
			if(obj==null){
				this.showErrorJson("不存在");
			}else{
				this.showSuccessJson("存在");
			}
			
		} catch (Exception e) {
			this.showErrorJson("不存在");

		}
		return this.JSON_MESSAGE;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	

}
