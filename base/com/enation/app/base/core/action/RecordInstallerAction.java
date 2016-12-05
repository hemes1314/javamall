package com.enation.app.base.core.action;

import java.util.Date;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.base.core.service.solution.Installer;
import com.enation.app.base.core.service.solution.InstallerManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.ip.IPSeeker;


/**
 * 记录产品使用者
 * @author kingapex
 * 2010-6-24下午05:16:05
 */ 
@ParentPackage("eop_default")
@Namespace("/api/base")
public class RecordInstallerAction extends WWAction {
		private InstallerManager installerManager;
		private String version;
		private String authcode;
		private String remark;
		private String domain;

		public String execute(){
			
			Installer installer  = new Installer();
			installer.setInstalltime(new Date().getTime());
			installer.setIp(this.getRequest().getRemoteAddr());
			installer.setRemark(this.getRequest().getServerName());
			installer.setVersion(version);
			installer.setRemark(remark);
			installer.setDomain(domain);
			installer.setArea( new IPSeeker() .getCountry(  installer.getIp() )  );
			this.installerManager.add(installer);
			
			this.json="{result:1}";
		 
			return this.JSON_MESSAGE;
		}
	//	
//		public String a(){
//			
//			Installer installer  = new Installer();
//			installer.setInstalltime(new Date().getTime());
//			installer.setIp(this.getRequest().getRemoteAddr());
//			installer.setRemark(this.getRequest().getServerName());
//			installer.setVersion(version);
//			installer.setAuthcode(authcode);
//			installer.setArea( new IPSeeker() .getCountry(  installer.getIp() )  );
//			installer.setDomain(this.getRequest().getParameter("domain"));
	// 
//			this.installerManager.add1(installer);
//			
//			this.json="{result:1}";
//			 
//			return this.JSON_MESSAGE;
//		}
	//	
	 
		public String list(){
			this.webpage = this.installerManager.list(this.getPage(),this.getPageSize());
			return "list";
		}
		
		public InstallerManager getInstallerManager() {
			return installerManager;
		}
		public void setInstallerManager(InstallerManager installerManager) {
			this.installerManager = installerManager;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}

		public String getAuthcode() {
			return authcode;
		}

		public void setAuthcode(String authcode) {
			this.authcode = authcode;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}
			
		
}
