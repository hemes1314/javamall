package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ErrorReport;
import com.enation.app.base.core.service.ErrorReportManager;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;

/**
 * 错误报告
 * @author kingapex
 * 2010-7-27下午05:26:47
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("errorReport")
@Results({
})
public class ErrorReportAction extends WWAction {
	
	private Integer id;
	private String error;
	private String info;
	private ErrorReportManager errorReportManager;
	private ErrorReport errorReport;
	
	public String execute(){
		try{
			ErrorReport report = new ErrorReport();
			report.setDateline( DateUtil.getDateline() );
			report.setError(error);
			report.setInfo(info);
			errorReportManager.add(report);
			
		}catch (Exception e) {
			 this.logger.error(e.getMessage(), e);
		}
		this.msgs.add("感谢您的提交，我们成功已经收到错误报告。");
		
		return this.MESSAGE;
	}


	public String proxy(){
		RemoteRequest remoteRequest = new RemoteRequest();
		remoteRequest.execute("http://www.enationsoft.com/eop/errorReport.do", this.getResponse(), this.getRequest());
		this.json=("感谢您的提交，我们成功已经收到错误报告。");
	 
		return this.JSON_MESSAGE;
		
	}
	
	public String list(){
		this.webpage =this.errorReportManager.list(this.getPage(), this.getPageSize());
		return "list";
	}
	
	public String view(){
		this.errorReport = this.errorReportManager.get(id);
		return "detail";
	}
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}


	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}


	public ErrorReportManager getErrorReportManager() {
		return errorReportManager;
	}


	public void setErrorReportManager(ErrorReportManager errorReportManager) {
		this.errorReportManager = errorReportManager;
	}


	public ErrorReport getErrorReport() {
		return errorReport;
	}


	public void setErrorReport(ErrorReport errorReport) {
		this.errorReport = errorReport;
	}
	
}
