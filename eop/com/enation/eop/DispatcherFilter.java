package com.enation.eop;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.enation.eop.processor.SafeHttpRequestWrapper;
import com.enation.eop.processor.back.BackendProcessor;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.processor.facade.FacadeProcessor;
import com.enation.eop.processor.facade.InstallProcessor;
import com.enation.eop.processor.facade.ResourceProcessor;
import com.enation.eop.resource.IAppManager;
import com.enation.eop.resource.model.EopApp;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopContextIniter;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.opensymphony.xwork2.ActionContext;

/**
 * Eop filter<br>
 * 负责前台模板的处理，以及后台模板的解析。<br>
 * 静态资源的处理<br>
 * @author kingapex
 * @version 1.0
 * @created 12-十月-2009 10:30:23
 * 
 * @version 2.0:
 * 1.简化类解构<br>
 * 2.不再由ioUtils post string而是直接由各个处理器自己post给HttpServlertResponse的流
 * 
 */
public class DispatcherFilter implements Filter {

	public void init(FilterConfig config) {

	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String uri = httpRequest.getServletPath();

		// api接口增加校验
		if ((uri.indexOf("/api/mobile") != -1) && (uri.indexOf("getSession") != -1)) {
		    if (httpRequest.getSession(false) == null) {
	            response.setContentType("text/json;charset=UTF-8");
	            PrintWriter out = response.getWriter();
	            out.print(JsonMessageUtil.expireSession());
	            out.flush();
	            return;
	        }
		}

		//不允许jsp被执行，防止被挂马
		if(uri.endsWith(".jsp")){
			return ;
		}
		
		//不允许properties被执行，防止泄漏隐私
		if(uri.endsWith(".properties")){
			return ;
		}
		
		EopContextIniter.init(httpRequest, httpResponse);
		
		IEopProcessor eopProcessor = null;
		
		//静态资源
		if(isExinclude(uri)){
			eopProcessor = new ResourceProcessor();
			boolean result = eopProcessor.process();
			if(!result){
				chain.doFilter(httpRequest, httpResponse);
			}
			return ;
		}
		//安装程序
		if (uri.startsWith("/install")	|| EopSetting.INSTALL_LOCK.toUpperCase().equals("NO")){
			boolean result = new InstallProcessor().process();
			if(!result){
				chain.doFilter(httpRequest, httpResponse);
			}
			return ;
		}
	
		
		
		//后台处理器
		if(uri.indexOf("/admin")>=0){
			
			IAppManager appManager = SpringContextHolder.getBean("appManager");
			//应用的后台
			List<EopApp> appList = appManager.list();
			String path = httpRequest.getServletPath();
			for (EopApp app : appList) {
				if (path.startsWith(app.getPath() + "/admin")) {
					eopProcessor=  new BackendProcessor();
				}
			}
		
			//登录后台
			if(uri.startsWith("/admin")){
				eopProcessor = new BackendProcessor();
			}
			
		}

		//config防止密码泄露
		if (uri.equals("/") || uri.equals("") || uri.startsWith("/config/")) {
			uri = "index.html";
		}
		
		//自动登录
		if(uri.endsWith(".html") || uri.endsWith(".do")){
			IEopProcessor processor = SpringContextHolder.getBean("autoLoginProcessor");
			processor.process();
		}
		
		//前台处理器
		if(uri.endsWith(".html")){
		
			
		
			eopProcessor= new FacadeProcessor();
		
		}
		httpRequest = ThreadContextHolder.getHttpRequest();
		if(eopProcessor==null ){
		  
			chain.doFilter(httpRequest, httpResponse);
			 
			 
		}else{
	 		boolean result =  eopProcessor.process(); //处理并返回结果，如果为false，则由其它filter处理
			
			if(!result){
				chain.doFilter(httpRequest, httpResponse);
			}
			 
		}
		ThreadContextHolder.remove();
		FreeMarkerPaser.remove();
		EopContext.remove(); 
		
	}

	public void destroy() {

	}
	
	private static boolean isExinclude(String uri) {
		String[] exts = new String[] { ".jpg", ".gif", ".js", ".png", ".css", ".doc", ".docx", ".xls",".xlsx", ".swf", ".ico" };
		for (String ext : exts) {
			if (uri.toLowerCase().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}
	
	
}