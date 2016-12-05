package com.enation.app.b2b2c.core.tag.store;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 检测URL是否正确
 * @author xulipeng
 *2015年1月4日14:25:02
 */

@Component
public class CheckUrlTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer show_c = (Integer) params.get("show_c");
		Integer store_id = (Integer) params.get("store_id");
		
		String ctx = this.getRequest().getContextPath();
		if("/".equals(ctx)){
			ctx="";
		}
		
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		if(show_c==null || show_c==0 || store_id==null || store_id==0 ){
			try {
				response.sendRedirect(ctx+"/404.html");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
