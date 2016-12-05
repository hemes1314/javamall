/**
 * 
 */
package com.enation.app.shop.component.pagecreator.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.output.FileWriterWithEncoding;

import com.enation.eop.processor.facade.FacadePageParser;
import com.enation.framework.util.StringUtil;

/**
 * 静态页面生成器
 * @author kingapex
 *2015-4-3
 */
public class GeneralPageCreator  extends FacadePageParser {
	private String pagePath;
	
	public GeneralPageCreator(String _pagePath){
		this.pagePath=_pagePath;
	}
	
	

	@Override
	protected Writer getWriter() throws IOException{
		
		//解决生成乱码的问题
		return new FileWriterWithEncoding(pagePath, "UTF-8");
//		return new FileWriter(pagePath);
	}
	

	@Override
	protected void outError(Exception e) {
		e.printStackTrace();
	}
	
	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	
	
}
