package com.enation.app.base.core.action.api;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/base")
@Action("tagTestCreate")
@Results({
})
public class TagTestCreateAction extends WWAction {
	
	private String content;
	private String params;
	private String filename;
	
	public String execute(){
		try{
			if(StringUtil.isEmpty(filename)){
				filename = createFileName()+".html";
			}
			String app_apth = StringUtil.getRootPath();
			String filepath=app_apth+"/docs/tags/runtime/"+filename;
			
			if(content==null){
				content="";
			}
			FileUtil.write(filepath, content);
			if(params==null){
				params="";
			}
			this.json = JsonMessageUtil.getStringJson("url", filename);
		}catch(Throwable e){
			this.logger.error("生成标签测试页面出错",e);
			this.showErrorJson("生成标签测试页面出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	private String createFileName(){
		String filename = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
		
		return filename+StringUtil.getRandStr(4);
	} 
	
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
}
