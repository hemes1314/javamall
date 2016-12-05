package com.gomecellar.workflow.model;

import com.enation.framework.database.PrimaryKeyField;

public class WF_Task_Opinion {
	private Integer id = 0;						//主键
	private String process_def_id = "";			//流程定义ID
	private Integer business_id = 0;			//业务ID
	private String process_instance_id = "";	//流程实例ID
	private String task_id = "";				//当前任务ID
	private String task_defination_key = "";	//环节定义ID
	private String user_id = "";				//处理人用户ID
	private String user_name = "";				//处理人用户名
	private String task_name = "";				//当前任务名称
	private Long create_time;					//到达时间
	private String comments = "";				//意见
	private String isagree  = "";				//是否同意
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProcess_def_id() {
		return process_def_id;
	}
	public void setProcess_def_id(String process_def_id) {
		this.process_def_id = process_def_id;
	}
	public Integer getBusiness_id() {
		return business_id;
	}
	public void setBusiness_id(Integer business_id) {
		this.business_id = business_id;
	}
	public String getProcess_instance_id() {
		return process_instance_id;
	}
	public void setProcess_instance_id(String process_instance_id) {
		this.process_instance_id = process_instance_id;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getIsagree() {
		return isagree;
	}
	public void setIsagree(String isagree) {
		this.isagree = isagree;
	}
	
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getTask_defination_key() {
		return task_defination_key;
	}
	public void setTask_defination_key(String task_defination_key) {
		this.task_defination_key = task_defination_key;
	}
}
