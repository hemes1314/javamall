package com.enation.framework.taglib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;


/**
 * freemarker 标签基类
 * @author kingapex
 *2013-7-28下午9:10:23
 */
public abstract class BaseFreeMarkerTag implements TemplateMethodModel {
	protected final Logger logger = Logger.getLogger(getClass());
	private int pageSize =10;
	@Override
	public Object exec(List jsonParam) throws TemplateModelException {
		
		if(jsonParam!=null && !jsonParam.isEmpty()){
			
			String param = (String)jsonParam.get(0);
			if(param!=null){
				 if(!param.startsWith("{")){
					 param="{"+param+"}";
				 }
				JSONObject jsonObject  =JSONObject.fromObject(param);
				Integer pageSizeNum =(Integer)jsonObject.get("pageSize");
				if(pageSizeNum!=null){
					this.pageSize= pageSizeNum;
				}
				return this.exec(jsonObject);
			}else{
				return this.exec(new HashMap());
			}
		}else{
			return this.exec(new HashMap());
		}
	}
	
	protected <T> T getBean(String beanid){
		return (T)SpringContextHolder.getBean(beanid);

	}
	
	protected abstract Object exec(Map params) throws TemplateModelException;

	protected int getPageSize() {
		return pageSize;
	}

	 

	protected int getPage() {
		int page =1;
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		page  = StringUtil.toInt(request.getParameter("page"),1);
		page = page < 1 ? 1 : page;
		return page;
	}
	
	protected HttpServletRequest getRequest(){
		return  ThreadContextHolder.getHttpRequest();
	}
	
}
