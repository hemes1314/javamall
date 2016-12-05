package com.gomecellar.workflow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;
import com.gomecellar.workflow.model.AppealFlow;
import com.gomecellar.workflow.model.WF_Task_Opinion;
import com.gomecellar.workflow.service.IAppealFlowManager;
import com.gomecellar.workflow.service.IAppealRelationManager;
import com.gomecellar.workflow.service.ITaskOpinionManager;
import com.gomecellar.workflow.utils.Constants;
import com.gomecellar.workflow.utils.HttpClientUtils;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Service
public class AppealFlowManager extends BaseSupport implements IAppealFlowManager {
	@Value("#{configProperties['address.workflowUrl']}")
	public String workflowUrl;

	@Autowired
	private ITaskOpinionManager taskOpinionManager;
	
	@Autowired
	private IAdminUserManager adminUserManager;
	
	@Autowired
	private IAppealRelationManager appealRelationManager;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int save(AppealFlow appealFlow) {
		appealFlow.setCreate_time(DateUtil.getDateline());
		appealFlow.setStatus(Constants.FLOW_STATUS_DRAFT);
		this.daoSupport.insert("es_appeal_flow", appealFlow);
		return this.daoSupport.getLastId("es_appeal_flow");
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void createAppealInfo(String businessId, String photo,
			String reason, long memberId,String memberName,String tradeno) {
		AppealFlow appealFlow = new AppealFlow();
		appealFlow.setBusiness_id(businessId);
		appealFlow.setAppeal_date(DateUtil.getDateline());
		appealFlow.setAppeal_photo(photo);
		appealFlow.setReason(reason);
		appealFlow.setMember_id(memberId);
		appealFlow.setMember_name(memberName);
		appealFlow.setTradeno(tradeno);
		this.save(appealFlow);
	}
	
	@Override
	public AppealFlow get(String id) {
		String sql ="select * from es_appeal_flow where appeal_flow_id=?";
		AppealFlow appealFlow = (AppealFlow)this.daoSupport.queryForObject(sql, AppealFlow.class, id);
		return appealFlow;
	}
	
	@Override
	public AppealFlow getByBusinessId(String businessId) {
	    
//		String sql ="select * from es_appeal_flow where business_id=? and status <>'-1' ";
//		AppealFlow appealFlow = (AppealFlow)this.daoSupport.queryForObject(sql, AppealFlow.class, businessId);
		// update by lxl 
	    String sql ="select * from es_appeal_flow where appeal_flow_id =? and status <>'-1' ";
	    AppealFlow appealFlow = (AppealFlow)this.daoSupport.queryForObject(sql, AppealFlow.class, businessId);

		return appealFlow;
	}
	
	@Override
	public AppealFlow getByProcessInsId(String processInstanceId) {
		String sql ="select * from es_appeal_flow where process_instance_id=?";
		AppealFlow appealFlow = (AppealFlow)this.daoSupport.queryForObject(sql, AppealFlow.class, processInstanceId);
		return appealFlow;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AppealFlow appealFlow) {
		this.daoSupport.update("es_appeal_flow", appealFlow, "appeal_flow_id="+appealFlow.getAppeal_flow_id());		
	}

	@Override
	public void delete(int id) {
		String sql="delete from es_appeal_flow where appeal_flow_id=?";
		this.daoSupport.execute(sql, id);
	}
	
	@Override
	public Page listAppeal(Map map, int page, int pageSize) {
		
		StringBuffer sql= new StringBuffer("select a.* from es_appeal_flow a where 1=1 ");
		
		if(map.get("keyword") != null) {
			sql.append(" and a.reason like '%" +map.get("keyword")  + "%' ");
		}
		sql.append(" and a.status='0' ");
		sql.append("  order by a.create_time desc ");
		Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		return webPage;
	}
	
	/**
	 * 判断业务是否存在
	 * @param businessId
	 * @return
	 */
	private boolean isExist(String businessId) {
		String sql ="select count(*) from es_appeal_flow where business_id=" + businessId + " and status <>'0' and status<>'-1' ";
		String r = this.daoSupport.queryForString(sql);
		return !"0".equals(r);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean startFlow(AppealFlow appealFlow) {
		
		boolean rel = false;
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("userId", appealFlow.getApplicant());			//发起人
		paramMap.put("processName", Constants.PROCESS_DEF_APPEAL_FLOW);	//流程定义ID
		paramMap.put("bussinessKey", String.valueOf(appealFlow.getAppeal_flow_id()));	//业务主键
		String title = "";	//流程标题
		if (appealFlow.getReason().length() > 18) {
			title = appealFlow.getReason().substring(0, 18) + "...";
		} else {
			title = appealFlow.getReason();
		}
			
		paramMap.put("title", title);

		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/startProcess", paramMap);

		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			String processInstanceId = "";
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				processInstanceId = ((Map)list.get(0).get("datas")).get("processInstanceId").toString();
				//保存程实例ID、更新流程创建时间、流程创建人信息
				appealFlow.setProcess_instance_id(processInstanceId);
				appealFlow.setStatus(Constants.FLOW_STATUS_APPROVAL);
				appealFlow.setCreate_time(DateUtil.getDateline());
				update(appealFlow);
				
				
				//保存审批意见
				WF_Task_Opinion opinion = new WF_Task_Opinion();
				opinion.setBusiness_id(appealFlow.getAppeal_flow_id());
				opinion.setProcess_def_id(Constants.PROCESS_DEF_APPEAL_FLOW);
				opinion.setTask_id("");
				opinion.setTask_name("流程启动");
				opinion.setTask_defination_key("start");
				opinion.setUser_id(appealFlow.getApplicant());
				opinion.setUser_name(appealFlow.getApplicant_cn());
				opinion.setProcess_instance_id(processInstanceId);
				opinion.setIsagree(Constants.IS_AGREE_YES);
				opinion.setCreate_time(DateUtil.getDateline());
				opinion.setComments("");
				taskOpinionManager.save(opinion);
				rel = true;
			}
		}	
		
		return rel;
	}
	
	@Override
	public void refuse(String id ,String businessId ,String tradeno) {
		//更新退货状态
		appealRelationManager.refuse(businessId,tradeno);
		
		//更新申诉状态
		AppealFlow appealFlow = this.getByBusinessId(String.valueOf(id));
		appealFlow.setStatus(Constants.FLOW_STATUS_CANCEL);
		this.update(appealFlow);
	}
	
	@Override
	public boolean stopFlow(String processInstanceId, String desc) {
		boolean rel = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("processInstanceId", processInstanceId);		
		paramMap.put("desc", desc);	
		
		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/delProcess", paramMap);
		System.out.println(result);
		
 		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				
				AppealFlow appealFlow = this.getByProcessInsId(processInstanceId);
				appealFlow.setStatus(Constants.FLOW_STATUS_CANCEL);
				this.update(appealFlow);
				
				//更新退货状态
				appealRelationManager.refuse(appealFlow.getBusiness_id(),appealFlow.getTradeno());
				
				//保存审批意见
				WF_Task_Opinion opinion = new WF_Task_Opinion();
				opinion.setBusiness_id(appealFlow.getAppeal_flow_id());
				opinion.setProcess_def_id(Constants.PROCESS_DEF_APPEAL_FLOW);
				opinion.setTask_id("");
				opinion.setTask_name("流程启动");
				opinion.setTask_defination_key("start");
				opinion.setUser_id(appealFlow.getApplicant());
				opinion.setUser_name(appealFlow.getApplicant_cn());
				opinion.setProcess_instance_id(processInstanceId);
				opinion.setIsagree(Constants.IS_AGREE_CANCEL);
				opinion.setCreate_time(DateUtil.getDateline());
				opinion.setComments(desc);
				taskOpinionManager.save(opinion);
				rel = true;
			}
		}
		return rel;
	}
	
	
	@Override
	public boolean claim(String taskId, String userId) {
		boolean rel = false;
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("taskId", taskId);		
		paramMap.put("userId", userId);	
		
		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/claim", paramMap);
		System.out.println(result);
		
		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				rel = true;
			}
		}
		return rel;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean refer(String businessId, String processInstanceId,
			String taskId, String taskDefinitionKey,String taskName, String userId, String userName,String comment,String isAgree){
		boolean rel = false;		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("processInstanceId", processInstanceId);			
		paramMap.put("comment", comment);	
		paramMap.put("taskId", taskId);	
		paramMap.put("title", "");
		paramMap.put("isAgree", isAgree);
		
		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/refer", paramMap);
		
		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				
				//保存审批意见
				WF_Task_Opinion opinion = new WF_Task_Opinion();
				opinion.setBusiness_id(Integer.valueOf(businessId));
				opinion.setProcess_def_id(Constants.PROCESS_DEF_APPEAL_FLOW);
				opinion.setTask_id(taskId);
				opinion.setTask_defination_key(taskDefinitionKey);
				opinion.setTask_name(taskName);
				opinion.setUser_id(userId);
				opinion.setUser_name(userName);
				opinion.setProcess_instance_id(processInstanceId);
				if (Constants.IS_AGREE_YES.equals(isAgree))
					opinion.setIsagree(Constants.IS_AGREE_YES);
				else
					opinion.setIsagree(Constants.IS_AGREE_NO);
				opinion.setCreate_time(DateUtil.getDateline());
				opinion.setComments(comment);
				taskOpinionManager.save(opinion);
				
				//流程审批结束，更新流程状态
				String isend = list.get(0).get("isend").toString();	
				if (isend.equals("true")) {
					AppealFlow appealFlow = this.get(businessId);
					appealFlow.setStatus(Constants.FLOW_STATUS_END);
					this.update(appealFlow);
					//更新退货单状态
					appealRelationManager.comlete(this.get(businessId).getTradeno());
				}
				
				rel = true;
			}
		} 
		
		return rel;
	}

	
	@Override
	public Page todoList(Map map, int page, int pageSize) {
		//通过远程接口请求服务
		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/todoList", map);
		
		int count = 0;	//总数
		List<Map<String,Object>> todoList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> relTodoList = new ArrayList<Map<String,Object>>();
		
		//返回结果处理
		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				todoList = (List<Map<String,Object>>)list.get(0).get("datas");
				count = Integer.valueOf(list.get(0).get("count").toString());
			}
		} 
		
		//取分页中的数据，补充流程以外的业务数据
		for (int i = (page - 1) * pageSize; i < page * pageSize - 1 && i < todoList.size(); i++) {
			Map<String,Object> tempMap = todoList.get(i);
			AppealFlow appealFlow = this.get((String)tempMap.get("businessKey"));
			if (appealFlow != null) {
				tempMap.put("member_name", appealFlow.getMember_name());
				tempMap.put("bill_appeal_date", appealFlow.getAppeal_date());
			}
			
			relTodoList.add(tempMap);
		}
		
		Page webPage = new Page(0, count, pageSize, relTodoList);
		
		return webPage;
	}
	
	@Override
	public Page doneList(Map map, int page, int pageSize) {
		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/doneList", map);
		
		int count = 0;
		List<Map<String,Object>> doneList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> relTodoList = new ArrayList<Map<String,Object>>();
		
		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				doneList = (List<Map<String,Object>>)list.get(0).get("datas");
				count = Integer.valueOf(list.get(0).get("count").toString());
			}
		} 
		
		//取分页中的数据，补充流程以外的业务数据
		for (int i = (page - 1) * pageSize; i < page * pageSize - 1 && i < doneList.size(); i++) {
			Map<String,Object> tempMap = doneList.get(i);
			AppealFlow appealFlow = this.get((String)tempMap.get("businessKey"));
			if (appealFlow != null) {
				tempMap.put("member_name", appealFlow.getMember_name());
				tempMap.put("bill_appeal_date", appealFlow.getAppeal_date());
			}
			
			relTodoList.add(tempMap);
		}
		
		Page webPage = new Page(0, count, pageSize, relTodoList);
		return webPage;
	}
	
	
	@Override
	public List getHistoricActivityList(String processInstanceId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("processInstanceId", processInstanceId);
		String result = HttpClientUtils.get(workflowUrl + "/restful/approval/getHistoricActivities", params);
		
		int count = 0;
		List<Map<String,Object>> activityList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> relTodoList = new ArrayList<Map<String,Object>>();
		
		if (!"".equals(result)) {
			List<Map<String,Object>> list = JsonUtil.toList("[" + result + "]");
			String status = list.get(0).get("status").toString();
			if ("0".equals(status)) {
				activityList = (List<Map<String,Object>>)list.get(0).get("datas");
				count = Integer.valueOf(list.get(0).get("count").toString());
			}
		} 
		
		
		for (int i = 0; i <activityList.size(); i++) {
			Map<String,Object> tempMap = activityList.get(i);
			AppealFlow appealFlow = this.get((String)tempMap.get("businessKey"));
			if (StringUtils.isNotEmpty((tempMap.get("assignee").toString()))) {
				tempMap.put("username", adminUserManager.get(NumberUtils.toLong(tempMap.get("assignee").toString())).getUsername());
			} else {
				tempMap.put("username","");
			}
			
			relTodoList.add(tempMap);
		}
		
		return relTodoList;
	}
	
	private String  createTempSql(Map map,String other,String order){
		String keyword = (String) map.get("keyword");
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		
		StringBuffer sql =new StringBuffer();
		sql.append("select * from es_appeal_flow where 1=1 ");
		
		if(keyword!=null){			
			sql.append(" or reason like '%"+keyword+"%')");
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and create_time>"+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and create_time<"+etime);
		}
				
		sql.append(" ORDER BY "+other+" "+order);
		
		return sql.toString();
	}

	

	
}
