package com.enation.app.base.core.action.api;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISmsManager;
import com.enation.framework.action.WWAction;

/**
 * 短信api
 * @author xulipeng
 *
 */

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("sms")
public class SmsApiAction extends WWAction {

	private ISmsManager smsManager;
	private String phone;
	private String content;
	
	public String send(){
		try {
			boolean flag = this.smsManager.send(phone, content);
			if(flag){
				this.showSuccessJson("发送成功");
			}else{
				this.showErrorJson("发送失败");
			}
		}catch (Exception e) {
			this.showErrorJson("发送失败，错误消息："+e.getMessage());
			this.logger.error("短信发送失败", e);
		}
		return JSON_MESSAGE;
	}

	public ISmsManager getSmsManager() {
		return smsManager;
	}

	public void setSmsManager(ISmsManager smsManager) {
		this.smsManager = smsManager;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
