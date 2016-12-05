package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.action.WWAction;
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("jmsMessage")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/cache/list.html") 
})
public class CacheAction extends WWAction {

	 public String execute(){
//		  CacheManager manager = CacheManager.getInstance();
//		  Cache  cache = manager.getCache("widgetCache");	
//		//  cache.setStatisticsEnabled(true);
//		 LiveCacheStatistics statistis = cache.getLiveCacheStatistics();
//		 boolean memory =statistis.isStatisticsEnabled();
//	 
		  return "list"; 
	 } 

}
