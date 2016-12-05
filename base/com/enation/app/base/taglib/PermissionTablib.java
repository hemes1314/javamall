package com.enation.app.base.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.taglib.EnationTagSupport;

/**
 * 权限标签
 * @author kingapex
 *
 */
public class PermissionTablib extends EnationTagSupport {

	private String actid; //权限点id

	
	@Override
	public int doStartTag() throws JspException {
		IPermissionManager permissionManager = SpringContextHolder.getBean("permissionManager");
		String[] arr = StringUtils.split(actid, ",");//actid.split(",");
		boolean result = false;
		for (String item : arr) {
			result = permissionManager.checkHaveAuth(PermissionConfig.getAuthId(item));
			if(result){
				break;
			}
		}
		 
		if(result){
			return this.EVAL_BODY_INCLUDE;
		}
		return this.SKIP_BODY;
	}

	public String getActid() {
		return actid;
	}

	public void setActid(String actid) {
		this.actid = actid;
	}

 
}
