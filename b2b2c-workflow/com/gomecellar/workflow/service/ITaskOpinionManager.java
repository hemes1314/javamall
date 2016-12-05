package com.gomecellar.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gomecellar.workflow.model.WF_Task_Opinion;

/**
 * 环节审批意见服务类
 * @author zhenguo
 *
 */
@Service
public interface ITaskOpinionManager {
	/**
	 * 保存环节处理意见
	 * @param opinion
	 * @return	主键
	 */
	public int save(WF_Task_Opinion opinion);
	
	/**
	 * 返回所有审批意见
	 * @param processId	流程定义Id
	 * @param businessId 业务Id
	 * @return
	 */
	public List list(String processDefId, String businessId);
}
