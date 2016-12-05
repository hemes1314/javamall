package com.gomecellar.workflow.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.gomecellar.workflow.model.AppealFlow;
import com.gomecellar.workflow.service.IAppealFlowManager;
import com.gomecellar.workflow.service.ITaskOpinionManager;
import com.gomecellar.workflow.utils.Constants;


@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Action("appeal")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/workflow/appeal_list.html"),
	 @Result(name="toAddPage",type="freemarker", location="/b2b2c/admin/workflow/appeal_add.html"),
	 @Result(name="toStartPage",type="freemarker", location="/b2b2c/admin/workflow/appeal_start.html"),
	 @Result(name="toEditPage",type="freemarker", location="/b2b2c/admin/workflow/appeal_edit.html"),
	 @Result(name="toApprovalPage",type="freemarker", location="/b2b2c/admin/workflow/appeal_approval.html"),
	 @Result(name="todolist",type="freemarker", location="/b2b2c/admin/workflow/appeal_todolist.html"),
	 @Result(name="toViewPage",type="freemarker", location="/b2b2c/admin/workflow/appeal_view.html"),
	 @Result(name="donelist",type="freemarker", location="/b2b2c/admin/workflow/appeal_donelist.html"),
	 @Result(name="historylist",type="freemarker", location="/b2b2c/admin/workflow/appeal_historylist.html")
})
public class AppealFlowAction extends WWAction {
	
	private AppealFlow appealFlow;
	private String appeal_flow_id = "" ;	//申诉主键
	private String appeal_date;
	private String business_id;	//提出需要申诉的业务，对应的主键
	
	//用户登录信息
	private String loginname;
	private String loginname_cn;
	
	//流程相关
	private String taskId;
	private String taskDefinitionKey;

	private String taskName;
	private String processInstanceId;


	//审核相关
	private String isagree;
	private String comments;
	private List opinionList;
	private List hisActivityList;
	
	//图片路径
	private List photoList;

	private String startTask = "0";
	private String kefuTask = "0";
	private String deptTask = "0";
	private String companyTask = "0";
	private String endTask = "0";


	//搜索条件
	private String keyword;
	private String start_time;
	private String end_time;
	
	//服务类
	private IAppealFlowManager appealFlowManager;
	private ITaskOpinionManager taskOpinionManager;
	
	/**
	 * 默认待提报流程列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	public String add(){
		getUserInfo();
		
		appealFlow = new AppealFlow();
		// 测试用
		appealFlow.setBusiness_id(String.valueOf((int)(Math.random() * 10000000)));
		appealFlow.setMember_id(110L);
		appealFlow.setMember_name("用户");
		appealFlow.setAppeal_date(DateUtil.getDateline());
		
		return "toAddPage";
	}

	/**
	 * 提报流程ajax
	 * @return
	 */
	public String listJson() {
	//	HttpServletRequest requst = ThreadContextHolder.getHttpRequest();

		Map map = new HashMap();
		map.put("keyword", keyword);
		map.put("start_time", start_time);
		map.put("end_time", end_time);
	
		this.webpage = this.appealFlowManager.listAppeal(map, this.getPage(),this.getPageSize());
		
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	
	/**
	 * 调整到填单界面
	 * @return
	 */
	public String toStartPage() {
		
		getUserInfo();
		
		//business_id = requst.getParameter("business_id");
		
		//按照业务主键加载申诉申请信息
		appealFlow = appealFlowManager.get(appeal_flow_id);
		if (appealFlow == null) {
			appealFlow = new AppealFlow();
		} else {
			if(StringUtils.isNotEmpty(appealFlow.getAppeal_photo())) {
				appealFlow.setAppeal_photo(UploadUtil.replacePath(appealFlow.getAppeal_photo()));
			}
		}
		
		
		return "toStartPage";
	}
	
	/**
	 * 流程启动
	 * @return
	 */
	public String start() {
		boolean result = appealFlowManager.startFlow(appealFlow);
		if (result) {
			this.showSuccessJson("申诉流程已提交成功");
		} else {
			this.showErrorJson("申诉流程启动失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 退回申诉
	 * @return
	 */
	public String refuse() {
		boolean result = false;
		try {
			appealFlowManager.refuse(appealFlow.getAppeal_flow_id().toString(),appealFlow.getBusiness_id(),appealFlow.getTradeno());
			result = true;
		} catch (Exception e) {}
				
		if (result) {
			this.showSuccessJson("申诉流程已退回成功");
		} else {
			this.showErrorJson("申诉流程退回失败");
		}
		
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 跳转到待办列表页面
	 * @return
	 */
	public String todolist() {
		 return "todolist";
	 }
	
	/**
	 * 待办列表Ajax
	 * @return
	 */
	public String todoListJson() {
		getUserInfo();
		
		Map map = new HashMap();
		map.put("userId", loginname);
		map.put("processKey", Constants.PROCESS_DEF_APPEAL_FLOW);
		
		this.webpage = this.appealFlowManager.todoList(map, this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 跳转到待办列表页面
	 * @return
	 */
	public String claim() {
		getUserInfo();
		boolean result = appealFlowManager.claim(taskId,loginname);
		if (result) {
			this.showSuccessJson("任务认领成功");
		} else {
			this.showErrorJson("任务认领失败");
		}
		return JSON_MESSAGE;
	 }
	
	
	/**
	 * 跳转到审批页面
	 * @return
	 */
	public String toApprovalPage() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		taskId = request.getParameter("id");
		taskName =  request.getParameter("name");
		
		processInstanceId = request.getParameter("processInstanceId");
		
		appealFlow = this.appealFlowManager.getByProcessInsId(processInstanceId);
		if (appealFlow == null) return null;
		if(StringUtils.isNotEmpty(appealFlow.getAppeal_photo())) {
			appealFlow.setAppeal_photo(UploadUtil.replacePath(appealFlow.getAppeal_photo()));
		}
		
		business_id = String.valueOf(appealFlow.getAppeal_flow_id());
		taskDefinitionKey = request.getParameter("taskDefinitionKey");
		
		//加载审核记录
		opinionList = taskOpinionManager.list(Constants.PROCESS_DEF_APPEAL_FLOW, business_id);
		
		//加载审批状态记录
		hisActivityList = appealFlowManager.getHistoricActivityList(processInstanceId);
		taskStatus(hisActivityList);
		return "toApprovalPage";
	}
	
	/**
	 * 设置任务节点颜色
	 * @param list
	 */
	private void taskStatus(List list) {
		for (int i = 0; i < list.size(); i++ ) {
			Map map = (Map)list.get(i);
			if (i == 0) {//根据最新处理节点标识每个人物节点颜色
				if("restartTask".equals(map.get("taskDefinitionKey")) || "startevent1".equals(map.get("taskDefinitionKey"))) {
					if (!map.get("endTime").equals("")) {
						startTask = "1";	//通过 绿色
					} else {
						startTask = "2";	//当前节点 红色
						kefuTask = "0";		//未到节点 灰色
					}
					
				} else if ("kefuTask".equals(map.get("taskDefinitionKey"))) {
					startTask = "1";	
					if (!map.get("endTime").equals("")) {
						kefuTask = "1";
					} else {
						kefuTask = "2";		
					}
				} else if ("deptTask".equals(map.get("taskDefinitionKey"))) {
					startTask = "1";	
					kefuTask = "1";	
					if (!map.get("endTime").equals("")) {
						deptTask = "1";
					} else {
						deptTask = "2";
					}
				} else if ("companyTask".equals(map.get("taskDefinitionKey"))) {
					startTask = "1";	
					kefuTask = "1";	
					deptTask = "1";
					if (!map.get("endTime").equals("")) {
						companyTask = "1";
						endTask = "1";
					} else {
						companyTask = "2";
					}
				} else {
					startTask = "1";	
					kefuTask = "1";	
					deptTask = "1";
					companyTask = "1";
					endTask = "1";
				}
			}
			
		}
	}
	
	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		AdminUser user = UserConext.getCurrentAdminUser();
		if (StringUtils.isNotEmpty(user.getRealname())) {
			loginname_cn = user.getRealname();
		} else {
			loginname_cn = user.getUsername();
		}
		loginname = String.valueOf(user.getUserid());
	}
	
	/**
	 * 流程审批 
	 * @return
	 */
	public String approval() {
		boolean result = false;
		
		getUserInfo();
		
		result = appealFlowManager.refer(appeal_flow_id, processInstanceId, taskId,taskDefinitionKey,taskName,loginname,loginname_cn,comments,isagree);
		
		if (result) {
			this.showSuccessJson("申诉流程已提交成功");
		} else {
			this.showErrorJson("申诉流程提交失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 终止流程
	 * @return
	 */
	public String stopFlow() {
		boolean result = appealFlowManager.stopFlow(processInstanceId, comments);
			
		
		if (result) {
			this.showSuccessJson("申诉流程已撤销成功");
		} else {
			this.showErrorJson("申诉流程撤销失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 跳转到已完成列表页面
	 * @return
	 */
	public String donelist() {
		 return "donelist";
	 }
	
	/**
	 * 已完成列表Ajax
	 * @return
	 */
	public String doneListJson() {
		getUserInfo();
		Map map = new HashMap();
		map.put("userId", loginname);
		map.put("processKey", Constants.PROCESS_DEF_APPEAL_FLOW);
		
		this.webpage = this.appealFlowManager.doneList(map, this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 跳转到查看页面
	 * @return
	 */
	public String toViewPage() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		processInstanceId = request.getParameter("processInstanceId");
		
		appealFlow = this.appealFlowManager.getByProcessInsId(processInstanceId);
		if(StringUtils.isNotEmpty(appealFlow.getAppeal_photo())) {
			appealFlow.setAppeal_photo(UploadUtil.replacePath(appealFlow.getAppeal_photo()));
		}
		business_id = String.valueOf(appealFlow.getAppeal_flow_id());
		
		
		//加载审核记录
		opinionList = taskOpinionManager.list(Constants.PROCESS_DEF_APPEAL_FLOW, business_id);
		//加载审批状态记录
		hisActivityList = appealFlowManager.getHistoricActivityList(processInstanceId);
		taskStatus(hisActivityList);
		
		return "toViewPage";
	}
	
	
	
	/**
	 * 暂存
	 * @return
	 */
	public String save() {
		/*if(!StringUtil.isEmpty(appeal_date)){
			appealFlow.setAppeal_date(DateUtil.getDateline(appeal_date));
		}*/
		appealFlowManager.save(appealFlow);
		this.showSuccessJson("保存成功");
		return JSON_MESSAGE;
	}

	 
	 

	public AppealFlow getAppealFlow() {
		return appealFlow;
	}

	public void setAppealFlow(AppealFlow appealFlow) {
		this.appealFlow = appealFlow;
	}

	public String getAppeal_flow_id() {
		return appeal_flow_id;
	}

	public void setAppeal_flow_id(String appeal_flow_id) {
		this.appeal_flow_id = appeal_flow_id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public IAppealFlowManager getAppealFlowManager() {
		return appealFlowManager;
	}

	public void setAppealFlowManager(IAppealFlowManager appealFlowManager) {
		this.appealFlowManager = appealFlowManager;
	}
	 
	 
	public String getAppeal_date() {
		return appeal_date;
	}


	public void setAppeal_date(String appeal_date) {
		this.appeal_date = appeal_date;
	}
	
	public String getBusiness_id() {
		return business_id;
	}


	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}
	
	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getLoginname_cn() {
		return loginname_cn;
	}

	public void setLoginname_cn(String loginname_cn) {
		this.loginname_cn = loginname_cn;
	}
	 
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	
	public String getIsagree() {
		return isagree;
	}

	public void setIsagree(String isagree) {
		this.isagree = isagree;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public List getOpinionList() {
		return opinionList;
	}

	public void setOpinionList(List opinionList) {
		this.opinionList = opinionList;
	}
	public ITaskOpinionManager getTaskOpinionManager() {
		return taskOpinionManager;
	}

	public void setTaskOpinionManager(ITaskOpinionManager taskOpinionManager) {
		this.taskOpinionManager = taskOpinionManager;
	}
	
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}
	public List getHisActivityList() {
		return hisActivityList;
	}

	public void setHisActivityList(List hisActivityList) {
		this.hisActivityList = hisActivityList;
	}
	
	public String getStartTask() {
		return startTask;
	}

	public void setStartTask(String startTask) {
		this.startTask = startTask;
	}

	public String getKefuTask() {
		return kefuTask;
	}

	public void setKefuTask(String kefuTask) {
		this.kefuTask = kefuTask;
	}

	public String getDeptTask() {
		return deptTask;
	}

	public void setDeptTask(String deptTask) {
		this.deptTask = deptTask;
	}

	public String getCompanyTask() {
		return companyTask;
	}

	public void setCompanyTask(String companyTask) {
		this.companyTask = companyTask;
	}
	
	public String getEndTask() {
		return endTask;
	}

	public void setEndTask(String endTask) {
		this.endTask = endTask;
	}
	public List getPhotoList() {
		photoList = new ArrayList();
		if (appealFlow != null) {
			String photos = appealFlow.getAppeal_photo();
			if (StringUtils.isNotEmpty(photos)) {
				String[] photoPaths = photos.split(",");
				for (String path : photoPaths) {
					photoList.add(path);
				}
			}
		}
		return photoList;
	}

	public void setPhotoList(List photoList) {
		this.photoList = photoList;
	}
}
