package com.enation.app.shop.core.action.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.component.payment.plugin.alipay.direct.AlipayDirectPlugin;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

@SuppressWarnings("serial")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("*refund-callback")
public class RefundCallbackApiAction extends WWAction {

	public  String execute() {
		try{
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String url = RequestUtil.getRequestUrl(httpRequest);
			System.out.println("验证是否退款回调函数------------------------------"+url);
			
			String pluginid = null;
			String ordertype =null;
			String[] params =this.getPluginid(url);
			
			String error = "参数不正确";
			if(params==null){
				this.json=error;
				return JSON_MESSAGE;
			}
			
			ordertype= params[0];
			pluginid= params[1];
			if (null == pluginid) {
				this.json=error;
				return JSON_MESSAGE;
			}  
			
			if (null == ordertype) {
				this.json=error;
				return JSON_MESSAGE;
			}  
			
			if (pluginid.equals("alipayDirectPlugin")) {
			    AlipayDirectPlugin alipay = SpringContextHolder.getBean(pluginid);
			    this.json= alipay.onRefundNotify(ordertype);
			
			    System.out.println(json);
			    this.logger.debug("支付回调结果"+json);
			} else {
			    throw new RuntimeException("Not supported plugin: "+pluginid);
			}
		}catch(Exception e){
		    e.printStackTrace();
			this.logger.error("支付回调发生错误",e);
			this.json = "error";
		}
		return JSON_MESSAGE;
		 
	}
	
	private String[] getPluginid(String url) {
		String pluginid = null;
		String ordertype= null;
		String[] params = new String[2];
		String pattern = ".*/(\\w+)_(\\w+)_(refund-callback).do(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			ordertype = m.replaceAll("$1");
			pluginid = m.replaceAll("$2");
			params[0]=ordertype;
			params[1]=pluginid;
			return params;
		} else {
			return null;
		}
	}
	
}
