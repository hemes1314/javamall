package com.enation.eop.processor.back;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.IEopProcessor;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;
/**
 * 后台模板处理器<br>
 * 负责权限的判断及后台模板的解析<br>
 * @author kingapex
 *2015-3-13
 */
public class BackendProcessor implements IEopProcessor {
	
	
	public boolean process() throws IOException{
		
		IAdminUserManager adminUserManager = SpringContextHolder.getBean("adminUserManager");
		AdminUser adminUser  = UserConext.getCurrentAdminUser();
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		HttpServletResponse httpResponse=  ThreadContextHolder.getHttpResponse();
		
		String ctx = httpRequest.getContextPath();
		
		if("/".equals(ctx)){
			ctx="";
		}
		String uri = httpRequest.getServletPath();
		if( uri.startsWith("/admin/backendUi!login.do")){ //登录界面放过
			return false;
		}
		
		if(uri.startsWith("/core/admin/adminUser!login.do")){//登录验证放过
			return false;
		}
		 
		
		String redirectUrl ="";
		if(uri.startsWith("/admin") ){
			if(adminUser==null){
				redirectUrl=(ctx+"/admin/backendUi!login.do");
			}else{ //已经登录过了
				if(!uri.startsWith("/admin/backendUi")){//如果不是访问的登录界，跳到登录界面
					httpResponse.sendRedirect(ctx+"/admin/backendUi!main.do");
					return true;
				}
				return false;
			}
			httpResponse.sendRedirect(redirectUrl);
			return true;
		}else{ // 访问应用下的功能
			
			if(adminUser==null){//超时了
				String referer = RequestUtil.getRequestUrl(httpRequest);
				httpResponse.sendRedirect(ctx+"/admin/backendUi!login.do?timeout=yes&referer="+referer);
				return true;
			}else{
				EopSite site=EopSite.getInstance();
				String product_type = EopSetting.PRODUCT;
				httpRequest.setAttribute("site",site);
				httpRequest.setAttribute("ctx",ctx);
				httpRequest.setAttribute("product_type",product_type);
				httpRequest.setAttribute("theme",getAdminTheme(site.getAdminthemeid() ));
				
			}
		}
		
		return false;
	}
	
	private String getAdminTheme(int themeid){
		
		IAdminThemeManager adminThemeManager =SpringContextHolder.getBean("adminThemeManager");
		// 读取后台使用的模板
		AdminTheme theTheme = adminThemeManager.get(themeid);
		String theme = "default";
		if (theTheme != null) {
			theme = theTheme.getPath();
		}
		return theme;
	}
	
}
