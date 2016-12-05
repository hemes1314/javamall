/**
 * 
 */
package com.enation.eop.processor.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 静态页解析器
 * @author kingapex
 *2015-3-26
 */
public class StaticPageParser {
    private static List<String> list = new ArrayList<String>();
    static {
        list.add("/index.html");
        list.add("/goods-(\\d+).html");
        list.add("/help-(\\d+)-(\\d+).html");
        list.add("/register.html");
        list.add("/login.html");
    }
    
	
	public  boolean parse(String uri) throws ServletException, IOException {
		
		HttpServletRequest httpRequest =ThreadContextHolder.getHttpRequest();
		HttpServletResponse httpResponse=ThreadContextHolder.getHttpResponse();
		
		//wap站不访问静态页
		if(SystemSetting.getWap_open()==1 && httpRequest.getServerName().equals( SystemSetting.getWap_domain() )){
			return false;
		}
		if("/".equals(uri)){
			uri="/index.html";
		}
		if(hasStatic(uri)){

			String path = this.getDispatcherPath(uri);
			//System.out.println("Static forward is :" + uri + ", convert to path:" + path);
			String realpath = httpRequest.getRealPath(path);
			//System.out.println("Static absolute is :" + realpath);
			File f = new File(realpath);
			//System.out.println("Exists:" + f.exists() + ", read:" + f.canRead() + ", write:" + f.canWrite() + ", execute:" + f.canExecute());
			//System.out.println("Forward:" + httpRequest.getClass());
		    //httpRequest.getRequestDispatcher(path).forward(httpRequest, httpResponse);  
			//解决weblogic下面对静态页访问无法forward的问题，绕过去了。
			httpResponse.setContentType("text/html");
			httpResponse.addHeader("Content-Length", "" + f.length());
			try (InputStream in = new FileInputStream(f)) {
			    IOUtils.copy(in, httpResponse.getOutputStream());    
			}
			
			return true ;
			
		}
		
		return false;
	}
	 
	/**
	 * 获取转发后的路径
	 * @param uri 原路径
	 * @return 如原路径为:/goods-1.html，则新路径为/html/goods/goods-1.html ，即将goods提取出来作为文件夹
	 * 如果是register.html这种不是伪静态的，则不提取为文件夹
	 */
	private String getDispatcherPath(String uri){
		String str ="/(\\w+)-(.*).html";
		Pattern pattern = Pattern.compile("^" + str+ "$", 2 | Pattern.DOTALL);
		Matcher m = pattern.matcher(uri);
		String folder ="";
		if(m.find()){
			folder= (m.group(1));
		}
		String path  = "/html/"+folder+uri;
		return  path;
	}
	
	public static void main(String[] args) {
		
		String str ="/(\\w+)-(.*).html";
		String uri="/goods-1.html";
		Pattern pattern = Pattern.compile("^" + str+ "$", 2 | Pattern.DOTALL);
		Matcher m = pattern.matcher(uri);
		if(m.find()){
			System.out.println(m.group(1));
		}
	}
	
	private boolean hasStatic(String uri){
		//2015-09-30 Ken：这里是固定的，放到静态变量里面。
	    //TODO: 另外这里写死了，后台开发者里面的URL映射就没用了吧？
		for (String string : list) {
			Pattern pattern = Pattern.compile("^" + string+ "$", 2 | Pattern.DOTALL);
			Matcher m = pattern.matcher(uri);
			if (m.find()){
				return true;
			}
			
		}
		
		return false;
	}
}
