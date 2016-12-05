package com.gomecellar.workflow.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.framework.database.Page;
import com.gomecellar.workflow.model.AppealFlow;

@SuppressWarnings("rawtypes")
@Service
public interface IAppealFlowManager {
	
	/**
	 * 保存
	 * @param AppealFlow
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int save(AppealFlow AppealFlow);
	
	
	/**
	 * 创建申诉申请
	 * @param businessId
	 * @param photo
	 * @param reason
	 * @return
	 */
	public void createAppealInfo(String businessId, String photo, String reason,long memberId,String memberName ,String tradeno);
	
	/**
	 * 更新
	 * @param AppealFlow
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AppealFlow AppealFlow);
	
	/**
	 * 删除
	 * @param id
	 */
	public void delete(int id);
	
	
	
	/**
	 * 获取实体类
	 * @param id
	 * @return
	 */
	public AppealFlow get(String id);
	
	/**
	 * 获取实体类
	 * @param businessId 申诉流程对应的业务主键
	 * @return
	 */
	public AppealFlow getByBusinessId(String businessId); 
	
	/**
	 * 获取实体类
	 * @param processInstanceId 流程实例ID
	 * @return
	 */
	public AppealFlow getByProcessInsId(String processInstanceId) ;
	
	/**
	 * 查询申诉申请列表
	 * @param map
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page listAppeal(Map map, int page, int pageSize); 
	
	/**
	 * 启动流程
	 * @param AppealFlow
	 * @return
	 */
	public boolean startFlow(AppealFlow AppealFlow);
	
	/**
	 * 申诉退回
	 * @param String
	 * @return
	 */
	public void refuse(String id,String  businessId,String tradeno);
	
	/**
	 * 终止流程
	 * @param processInstanceId
	 * @param desc
	 * @return
	 */
	public boolean stopFlow(String processInstanceId, String desc);
	
	/**
	 *  审批 
	 * @param business_id	审批流程主键
	 * @param processInstanceId
	 * @param taskId
	 * @param taskName
	 * @param comment
	 * @param isAgree 1:同意，0：不同意
	 * @return
	 */
	public boolean refer(String business_id, String processInstanceId,
			String taskId, String taskDefinitionKey, String taskName,
			String userId, String userName, String comment, String isAgree);

	/**
	 * 认领任务
	 * @param taskId
	 * @param userId
	 * @return
	 */
	public boolean claim(String taskId, String userId);
	
	/**
	 * 获取待办任务列表
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param other
	 * @param order
	 * @return
	 */
	public Page todoList(Map map, int page, int pageSize);
	
	/**
	 * 获取已完成任务列表
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param other
	 * @param order
	 * @return
	 */
	public Page doneList(Map map, int page, int pageSize);
	
	/**
	 * 获取流程环节审批状态信息
	 * @param processInstanceId 流程实例ID
	 * @return
	 */
	public List getHistoricActivityList(String processInstanceId);



	
}
