package com.enation.app.base.core.service;

import com.enation.app.base.core.model.ErrorReport;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;

public class ErrorReportManager  {
	private IDaoSupport<ErrorReport>  daoSupport;
 
	
	public void add(ErrorReport report){
		this.daoSupport.insert("eop_error_report", report);
	}
	
	public ErrorReport get(Integer id){
		return this.daoSupport.queryForObject("select * from eop_error_report where id=?", ErrorReport.class, id);
	}
	
	public Page list(int pageNo,int pageSize){
		
		return this.daoSupport.queryForPage("select * from eop_error_report order by dateline desc", pageNo, pageSize);
	}
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	
	
}
