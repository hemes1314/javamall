package com.enation.eop.sdk.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enation.eop.SystemSetting;
import com.enation.eop.processor.SafeHttpRequestWrapper;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * Eop上下文初始化
 * @author kingapex
 *
 */
public class EopContextIniter {
	
	public static void init(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		FreeMarkerPaser.set(new FreeMarkerPaser());
		FreeMarkerPaser fmp = FreeMarkerPaser.getInstance();
		
		httpRequest = new SafeHttpRequestWrapper(httpRequest);
		
		/**
		 * 将requst response及静态资源域名加入到上下文
		 */
		HttpSession session = httpRequest.getSession();
		ThreadContextHolder.getSessionContext().setSession(session);
		ThreadContextHolder.setHttpRequest(httpRequest);
		ThreadContextHolder.setHttpResponse(httpResponse);
		httpRequest.setAttribute("staticserver", SystemSetting.getStatic_server_domain());
		EopContext context = new EopContext();

		String servletPath = httpRequest.getServletPath();

		if (servletPath.startsWith("/statics"))
			return;

		if( servletPath.startsWith("/install") ){
		 
		}else{
		 
			fmp.putData("site", EopSite.getInstance() );
		}
		EopContext.setContext(context);

		/**
		 * 设置freemarker的相关常量
		 */
		fmp.putData("ctx", httpRequest.getContextPath());
	}
	
 
}
