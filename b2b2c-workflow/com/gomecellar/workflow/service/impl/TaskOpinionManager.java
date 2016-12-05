package com.gomecellar.workflow.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.eop.sdk.database.BaseSupport;
import com.gomecellar.workflow.model.WF_Task_Opinion;
import com.gomecellar.workflow.service.ITaskOpinionManager;

@Service
public class TaskOpinionManager extends BaseSupport<WF_Task_Opinion> implements
		ITaskOpinionManager {

	@Override
	public int save(WF_Task_Opinion opinion) {
		this.daoSupport.insert("es_wf_task_opinion", opinion);
		return this.daoSupport.getLastId("es_wf_task_opinion");
	}
	
	@Override
	public List list(String processDefId, String businessId) {
		String sql = "select * from es_wf_task_opinion where process_def_id =? and business_id=?   order by create_time desc";
		List list = this.baseDaoSupport.queryForList(sql, processDefId, businessId);
		return list;
	}
}
