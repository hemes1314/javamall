package com.enation.eop.processor.facade;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Theme;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.FreeMarkerUtil;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.TagCreator;
import com.enation.framework.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 前台页面解析器<br>
 * 解析前台模板<br>
 * 
 * v1:@author kingapex 2010-2-8下午10:45:20
 * v2:@author kingapex 2015-3-11<br>
 * v2更新：改由freemarker直接post到输出流<br>
 * 完全去掉挂件解析
 */
public class FacadePageParser  {
	
	long threadid = 0;
	public  boolean parse(String uri) {
		try {
		
			if(EopSite.getInstance().getSiteon()==0){
				 getCloseHtml();
				 return true;
			}
			
			return doParse(uri);
			
			 
		} catch (UrlNotFoundException e) {
			HttpServletResponse httpResponse = ThreadContextHolder.getHttpResponse();
			if(httpResponse!=null){
				httpResponse.setStatus(HttpHeaderConstants.status_404);
			}
			return get404Html();
		} 
	}
	
	private boolean get404Html() {

		// 要设置到页面中的变量值
		Map<String, Object> widgetData = new HashMap<String, Object>();
		widgetData.put("site", EopSite.getInstance());
		String originalUri = "/404.html"; 
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		request.setAttribute("pageid", "404"); // 设置模板的名称为pageid至上下文中，供其它包装器调用
		request.setAttribute("tplFileName", "404");// 设置模板名称至上下文		

		return  parse(originalUri);
	}
	
	//暂停关闭网站
	private void getCloseHtml(){

		// 要设置到页面中的变量值
		Map<String, Object> widgetData = new HashMap<String, Object>();
		widgetData.put("site", EopSite.getInstance());
		String originalUri = "/close.html"; 

		 doParse(originalUri);
	}
	
	private boolean doParse(String uri) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ctx =request.getContextPath();
		if(  ctx.equals("/")){
			ctx="";
		}
		
		if(!StringUtil.isEmpty(ctx)){
			uri = uri.replaceAll(ctx, "");
		}
		

		// 去掉uri问号以后的东西
		if (uri.indexOf('?') > 0) { 
			uri = uri.substring(0, uri.indexOf('?'));
		}
		
		//如果是目录，带上/index.html
		if(!uri.endsWith(".html")){
			if(uri.endsWith("/")){
				uri=uri+"index.html";
			}else{
				uri=uri+"/index.html";
			}
		}

		// 得到模板文件名
		IThemeUriManager themeUriManager = SpringContextHolder.getBean("themeUriManager");
		ThemeUri themeUri = themeUriManager.getPath(uri);
	 
		String tplFileName =uri;

		
		if(themeUri!=null){
			 tplFileName = themeUri.getPath();
		}else{
			
		}
		
		if(tplFileName.equals("/")){
			tplFileName="/index.html";
		}
		EopSite site = EopSite.getInstance();
		String pageid = tplFileName.substring(1, tplFileName.indexOf("."));
//		request.setAttribute("pageid", pageid); // 设置模板的名称为pageid至上下文中，供其它包装器调用
//		request.setAttribute("tplFileName", pageid);// 设置模板名称至上下文
		if(themeUri!=null){
			if (!StringUtil.isEmpty( themeUri.getPagename()) || !StringUtil.isEmpty( themeUri.getKeywords()) || !StringUtil.isEmpty(themeUri.getDescription())) {
				FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
	
				if (!StringUtil.isEmpty(themeUri.getPagename()))
					
					freeMarkerPaser.putData(HeaderConstants.title, themeUri.getPagename()+"-"+(StringUtil.isEmpty(site.getTitle())?site.getSitename():site.getTitle()));
	
				if (!StringUtil.isEmpty(themeUri.getKeywords()))
					freeMarkerPaser.putData(HeaderConstants.keywords, themeUri.getKeywords());
	
				if (!StringUtil.isEmpty(themeUri.getDescription()))
					freeMarkerPaser.putData(HeaderConstants.description, themeUri.getDescription());
			}
		}
		
		 
 		
		
		return  parseTpl(tplFileName); 
	}

 
	
	private boolean parseTpl(String tplFileName) {
		String themePath = null;
		try {
			EopSite site = EopSite.getInstance();
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			
			//开启了wap，并且是手机访问，且不是wap 域名，则强制跳到wap域名
			if(SystemSetting.getWap_open()==1&&  !request.getServerName().equals(SystemSetting.getWap_domain() ) &&  this.isMobile()){
				try {
					
					ThreadContextHolder.getHttpResponse().getWriter().write("<script>location.href='"+getWapDomainUrl()+"'</script>");
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				 return true;
			}
		 
			if(SystemSetting.getWap_open()==1 && request.getServerName().equals(SystemSetting.getWap_domain()) ){
				 
				themePath= SystemSetting.getWap_folder();
			    
			}else{
				
				
				IThemeManager themeManager = SpringContextHolder.getBean("themeManager");
				Integer themeid  = site.getThemeid();
				if(themeid==null){
					System.out.println("发生 theme id 为空！！，此时请求路径为:"+request.getRequestURI()+"，模板文件为"+tplFileName);
					System.out.println(" themeid暂时重置为 1");
					themeid=1;
				}
				Theme theme = themeManager.getTheme(themeid);
				themePath = theme.getPath();
			}
			
			
			String static_server_domain =SystemSetting.getStatic_server_domain(); //静态资源服务器域名
			
			int sms_isopen = SystemSetting.getSms_reg_open();
			
			String rootpath= StringUtil.getRootPath();
			
			String themeFld =rootpath+ "/themes/" + themePath;
			
			Map  widgetData = new HashMap();
			
			widgetData.put("staticserver", static_server_domain);
			widgetData.put("logo", site.getLogofile());
			widgetData.put("ctx", request.getContextPath());
			
            Member member = UserConext.getCurrentMember();
            if(member==null){       //判断是否登陆
                widgetData.put("member", member);
            }else{
                
                IMemberManager memberManager = SpringContextHolder.getBean("memberManager");
                Member data_member = memberManager.get(member.getMember_id());
                
                //判断用户是否被手动删除
                if(data_member==null){
                    ThreadContextHolder.getSessionContext().removeAttribute(UserConext.CURRENT_MEMBER_KEY);
                }
                widgetData.put("member", member);   
            }		

			widgetData.put("site", site);
			widgetData.put("sms_isopen", sms_isopen);

			Enumeration<String> paramNames =  request.getParameterNames();
			if(paramNames!=null ){
				while( paramNames.hasMoreElements()){
					String name = paramNames.nextElement();
					String value = request.getParameter(name);
					widgetData.put(name,value);
				}
			
			}
			widgetData.put("newTag", new TagCreator());
			
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);
			Template temp = cfg.getTemplate(tplFileName);
		
			
			temp.process(widgetData,getWriter());
			//Writer writer =  new PrintWriter(System.out) ;
			//temp.process(widgetData, writer);
		} catch (Exception e) {
			if(e instanceof FileNotFoundException ||  e instanceof UrlNotFoundException||e.getCause()   instanceof UrlNotFoundException){
				if( tplFileName.equals("/404.html")){
					try {
						ThreadContextHolder.getHttpResponse().getWriter().write("访问的地址未找到，试图用404页面展示，但在模板["+themePath+"]下未找到模板页：[404.html]。");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					throw new UrlNotFoundException();
				}
				
			}
			outError(e);
		}
 
		return true;
	}
	
	
	//检测是不是手机访问
	private static boolean isMobile(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if(request==null) return false;
		 String user_agent = request.getHeader("user-agent");
		 if(StringUtil.isEmpty(user_agent)) return false;
		 
		String userAgent = user_agent.toLowerCase();

		if(userAgent.contains("android" ) || userAgent.contains("iphone"))
		{
			return true;
		}
		return false;
		
		 
	}
	
	
	
	/**
	 * 输出错误
	 * @param e
	 */
	protected void outError(Exception e){
		ThreadContextHolder.getHttpResponse().setStatus(HttpHeaderConstants.status_500);
		try {
			e.printStackTrace(ThreadContextHolder.getHttpResponse().getWriter());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 获取页面html输出的writer，子类可覆写此方法以改变输出目标
	 * @return
	 * @throws IOException
	 */
	protected Writer getWriter() throws IOException{
		return ThreadContextHolder.getHttpResponse().getWriter();
	}
	
 

	
	public static void main(String[] args) throws IOException, TemplateException {
		Configuration cfg = FreeMarkerUtil.getFolderCfg("/Users/kingapex/workspace/v40/javamall/WebContent/themes/default/");
		Template temp = cfg.getTemplate("test.html");
		Map  widgetData = new HashMap();
		Writer writer =  new PrintWriter(System.out) ;
		temp.process(widgetData, writer);
	}

	private boolean matchUrl(String uri, String targetUri) {
		Pattern p = Pattern.compile(targetUri, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(uri);
		return m.find();
	}
	
	
	public static String getWapDomainUrl(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int port = request.getServerPort();
		
		String portstr="";
		if(port!=80){
			portstr=":"+port;
		}
		String contextPath = request.getContextPath();
		if(contextPath.equals("/")){
			contextPath="";
		}
	 
		String severname= SystemSetting.getWap_domain();
		String url  = "http://"+severname+portstr+contextPath+"/index.html";
		return url;
		
	}
 
	
}