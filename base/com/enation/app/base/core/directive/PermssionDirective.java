package com.enation.app.base.core.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.framework.context.spring.SpringContextHolder;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class PermssionDirective implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		IPermissionManager permissionManager = SpringContextHolder.getBean("permissionManager");
		String  actid =params.get("actid").toString();
		String[] arr = StringUtils.split(actid, ",");//actid.split(",");
		boolean result = false;
		for (String item : arr) {
			result = permissionManager.checkHaveAuth(PermissionConfig.getAuthId(item));
			if(result){
				break;
			}
		}
		 
		if(result)
		 body.render(env.getOut());
	}

}
