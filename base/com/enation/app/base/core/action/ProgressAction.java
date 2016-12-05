/**
 * 
 */
package com.enation.app.base.core.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.ExceptionMapping;
import org.apache.struts2.convention.annotation.ExceptionMappings;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.base.core.model.TaskProgress;
import com.enation.app.base.core.service.ProgressContainer;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 进度action
 * @author kingapex
 *2015-5-13
 */

@ParentPackage("eop_default")
@Namespace("/core/admin")
public class ProgressAction extends WWAction {

	
	private String progressid;
	/**
	 * 检测是否有任务正在进行
	 * @return
	 */
	public String hasTask(){
		
		if( StringUtil.isEmpty(progressid) ){
			this.showErrorJson("progressid 不能为空"+this );
			return this.JSON_MESSAGE;
		}
		
		
		int hastask = ProgressContainer.getProgress(progressid)==null?0:1;
		this.json= JsonMessageUtil.getNumberJson("hastask", hastask);
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 查看生成进度
	 * @return
	 */
	public String viewProgress(){
		 
		if( StringUtil.isEmpty(progressid) ){
			this.showErrorJson("progressid 不能为空"+this);
			return this.JSON_MESSAGE;
		}
		
		TaskProgress taskProgress =  ProgressContainer.getProgress(progressid);
		
		if( taskProgress== null ){
			System.out.println("is null");
			taskProgress= new TaskProgress(100);
		 
		}
		
		
		this.json= JsonMessageUtil.getObjectJson(taskProgress);
		
		if(taskProgress.getTask_status()!=0){ //出错 或成功了，移除此次任务
			ProgressContainer.remove(progressid);
		}
		
		return this.JSON_MESSAGE;
	}


	public String getProgressid() {
		return progressid;
	}


	public void setProgressid(String progressid) {
		this.progressid = progressid;
	}
	
	
	
}
