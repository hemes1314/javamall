package com.enation.app.shop.core.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * url正则表达式匹配标签
 * @author kingapex
 *2013-7-31下午9:35:41
 */
@Component
@Scope("prototype")
public class UrlRegMatchTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		String reg = (String)params.get("reg");
	
		HttpServletRequest request = this.getRequest();
		String url = request.getRequestURI();
	
		if(StringUtil.isEmpty(url)){
			return null;
		}
		
		Pattern p = Pattern.compile(reg, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			return m.group();			
		}else{
			return null;
		}
		
		
	}
	
	public static void main(String[] args){
		Pattern p = Pattern.compile("(\\d+)", 2 | Pattern.DOTALL);
		Matcher m = p.matcher("/21-goods-12.html");
		if (m.find()) {
		 
		
			int count =m.groupCount();
			String[] ar =  new String[count];
			for(int i=0;i<=count;i++){
				//System.out.println(m.group(i));
			//	ar[i] =m.group(i);
			//	//System.out.println(ar[i]);
			}
	 
			
		}
		
 
		
	}

	
	
}
