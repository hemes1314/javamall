package com.enation.app.base.core.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.solution.ISolutionInstaller;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.util.DateUtil;

public class EopInstallManager {
 
	private ISolutionInstaller solutionInstaller;
	private IAdminUserManager adminUserManager;
	public void install(String username, String password,String productid) {
		long s= DateUtil.getDateline();
		
	    long start  = this.log("开始安装");
		DBSolutionFactory.dbImport("file:com/enation/app/base/init.xml","");		
		long end =this.logEnd("init xml安装完成",start);		
		 
		
		solutionInstaller.install( productid);
		end =this.logEnd("simple product 安装完成",end);		
		
		solutionInstaller.install( "base");
		end =this.logEnd("base product 安装完成",end);	
		
		this.installUser(username, password);
		 end  = this.logEnd("user 安装完成",end);
		 
		System.out.println("耗时{"+(end-s)+"}");
	}

	private long log(String msg){
		long now  = DateUtil.getDateline();
		System.out.println(msg+"["+DateUtil.toString(System.currentTimeMillis(), "HH:MM:ss")+"]");
		return now;
	}
	private long logEnd(String msg,long start){
		long now  = DateUtil.getDateline();
		System.out.println(msg+"["+DateUtil.toString(System.currentTimeMillis(), "HH:MM:ss")+"],耗时【"+(now-start)+"】");
		return now;
	}
	/*
	 * 安装管理员
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void installUser(String username, String password) {
		
		//超级管理员
		AdminUser adminUser = new AdminUser();
		adminUser.setUsername(username);
		adminUser.setPassword(password);
		adminUser.setState(1);
		adminUser.setFounder(1);		
		this.adminUserManager.add(adminUser);
		

		//添加产品部
		AdminUser chanpin = new AdminUser();
		chanpin.setUsername("chanpin");
		chanpin.setPassword("123456");
		chanpin.setState(1);
		chanpin.setRoleids(new int[]{2});
		adminUserManager.add(chanpin);
		
		//添加库管
		AdminUser kuguan = new AdminUser();
		kuguan.setUsername("kuguan");
		kuguan.setPassword("123456");
		kuguan.setState(1);
		kuguan.setRoleids(new int[]{3});
		adminUserManager.add(kuguan);
		
		//添加财务
		AdminUser caiwu = new AdminUser();
		caiwu.setUsername("caiwu");
		caiwu.setPassword("123456");
		caiwu.setState(1);
		caiwu.setRoleids(new int[]{4});
		adminUserManager.add(caiwu);
		
		//添加财务
		AdminUser kefu = new AdminUser();
		kefu.setUsername("kefu");
		kefu.setPassword("123456");
		kefu.setState(1);
		kefu.setRoleids(new int[]{5});
		adminUserManager.add(kefu);
		
	}

	public ISolutionInstaller getSolutionInstaller() {
		return solutionInstaller;
	}

	public void setSolutionInstaller(ISolutionInstaller solutionInstaller) {
		this.solutionInstaller = solutionInstaller;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	
	
 

}
