package com.enation.app.b2b2c;

import org.apache.struts2.convention.annotation.Action;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.ICartManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.App;

/**
 * b2b2c应用
 * @author xulipeng
 * 2015年1月8日22:31:23
 */

@Component("b2b2c")
public class B2b2cApp extends App {

 
	@Override
	public String getId() {
		return "b2b2c";
	}

	@Override
	public String getName() {
		return "b2b2c应用";
	}

	@Override
	public String getNameSpace() {
		return "/b2b2c";
	}

	@Override
	public void install() {
		this.doInstall("file:com/enation/app/b2b2c/b2b2c.xml");
		
	}

 

	@Override
	public void sessionDestroyed(String sessionid, EopSite site) {
		
		
	}
 

	
}
