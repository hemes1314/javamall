package com.gomecellar.workflow.service;

import org.springframework.stereotype.Service;

/**
 * 申诉申请与退货业务关联服务类
 * @author zhenguo
 *
 */
@Service
public interface IAppealRelationManager {
	/**
	 * 申诉退回
	 * @param AppealFlow
	 * @return
	 */
	public void refuse(String businessId ,String tradeno);
	
	/**
	 * 申诉完成
	 * @param AppealFlow
	 * @return
	 */
	public void comlete(String businessId);
	
}
