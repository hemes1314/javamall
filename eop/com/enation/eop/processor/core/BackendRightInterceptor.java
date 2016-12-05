package com.enation.eop.processor.core;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

/**
 * 后台权限拦截器
 * @author kingapex
 *
 */
public class BackendRightInterceptor extends MethodFilterInterceptor {

	@Override
	protected String doIntercept(ActionInvocation inv) throws Exception {
		IAdminUserManager adminUserManager = SpringContextHolder.getBean("adminUserManager");
		AdminUser user  = UserConext.getCurrentAdminUser();
		if(user==null){
			return "not_login";
		}
		
		String result = inv.invoke();
		return result;
	}

}
