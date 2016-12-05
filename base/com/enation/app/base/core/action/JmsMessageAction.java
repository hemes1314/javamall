package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;

import com.enation.framework.action.WWAction;
import com.enation.framework.jms.EopProducer;
import com.enation.framework.jms.IEopJmsMessage;
import com.enation.framework.jms.ITaskView;
import com.enation.framework.jms.TaskContainer;
import com.enation.framework.jms.TaskView;


/**
 * Jms消息action
 * @author kingapex
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("jmsMessage")
@Results({
})
public class JmsMessageAction extends WWAction {
	 
 
	private EopProducer eopProducer;
	
	private String taskid;
	
	public String list(){
		
		try{
			Collection<ITaskView> 	taskList =TaskContainer.listTask();
			List<ITaskView> tempList = new ArrayList<ITaskView>();
			for(ITaskView task:taskList){
				tempList.add( new TaskView(task));
			}
			String listStr = JSONArray.fromObject(tempList).toString();		 
			json=  "{\"result\":1,\"data\":"+listStr+"}";
		}catch(RuntimeException e){
			e.printStackTrace();
			this.logger.error("读取jms消息出错",e);
			this.showErrorJson("读取Jms消息出错:["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	
	public String remove(){
		try{
			TaskContainer.removeTask(taskid);
			this.showSuccessJson("移除任务成功");
		}catch(RuntimeException e){
			this.logger.error("移除任务出错",e);
			this.showErrorJson("移除任务出错["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	
	public String retry(){
		
		try{
			ITaskView taskView = TaskContainer.getTask(taskid);
			if( taskView instanceof IEopJmsMessage ){
				IEopJmsMessage message = (IEopJmsMessage)taskView;
				eopProducer.send(message);
			}
			this.showSuccessJson("任务["+taskid+"]下达成功!");
		}catch(Exception e){
			this.logger.error("下达任务失败",e);
			this.showErrorJson(e.getMessage());
		}
		
		return this.JSON_MESSAGE;
		
	}
	
	

 

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public EopProducer getEopProducer() {
		return eopProducer;
	}

	public void setEopProducer(EopProducer eopProducer) {
		this.eopProducer = eopProducer;
	}
	
}
