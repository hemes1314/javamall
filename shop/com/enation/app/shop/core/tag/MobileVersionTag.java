package com.enation.app.shop.core.tag;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class MobileVersionTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		 
		HttpServletRequest httpRequest =ThreadContextHolder.getHttpRequest();  
		String userAgent = httpRequest.getHeader("user-agent").toLowerCase();
		Map result = new HashMap(2);
		String version =this.isIphone(userAgent);
		if(!"false".equals(version)){
			result.put("type", "iphone");
		 
			String[] vesions = version.split("_");
			Double dv = Double.valueOf(vesions[0]+"."+vesions[1]+vesions[2]);
			result.put("version", dv);
			return result;
		}
				 
		result.put("type","pc");
		result.put("version",7);
		return result;
	}
	
	private String isIphone(String userAgent ){
		String pattern="(\\(.*iphone\\s+[^\\(]*\\))";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(userAgent);
		if(m.find()){
			String versionStr= m.group(1);
			pattern="os\\s+(\\w+)\\s+";
			p=Pattern.compile(pattern, 2 | Pattern.DOTALL);
			m = p.matcher(versionStr);
			if(m.find()){
				return (m.group(1));
			}
		}
		return "false";
	}
	
	public static void main(String[] args) {
		String str="mozilla/5.0 (iphone; cpu iphone os 6_1_4 like mac os x) applewebkit/536.26 (khtml, like gecko) mobile/10b350 qq/4.2.2.10";
		String pattern="(\\(.*iphone\\s+[^\\(]*\\))";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(str);
		if(m.find()){
			String versionStr= m.group(1);
			pattern="os\\s+(\\w+)\\s+";
			p=Pattern.compile(pattern, 2 | Pattern.DOTALL);
			m = p.matcher(versionStr);
			if(m.find()){
				String v = m.group(1);
				String[] vesions = v.split("_");
				Double version = Double.valueOf(vesions[0]+"."+vesions[1]+vesions[2]);
				//System.out.println(version);
			}
		}
	}

}
