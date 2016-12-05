package com.enation.app.b2b2c.core.service.order.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreSellBackManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
@Component
public class StoreSellBackManager implements IStoreSellBackManager {
	private IDaoSupport daoSupport;
	
	@Override
	public Page list(int page, int pageSize, Integer store_id,Integer status,Map map) {
		Long start_time= (Long)map.get("start_time");
		Long end_time= (Long)map.get("end_time");
		StringBuffer sql = new  StringBuffer("select * from es_sellback_list where  store_id=? ");
		if(status!=null){
			sql.append(" and tradestatus="+status);
		}
		if(start_time!=null){
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00");
			sql.append(" and regtime>"+stime);
		}
		if(end_time!=null){
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and regtime<"+etime);
		}
		
		// lxl  update  tradestatus 为-1 的是在工作流审核中
		sql.append(" and tradestatus != -1 order by id desc ");
		//System.out.println(sql.toString());
		Page webpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize,store_id);
		return webpage;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
