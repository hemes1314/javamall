/**
 * 
 */
package com.enation.app.shop.component.pagecreator.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.enation.eop.processor.facade.FacadePageParser;
import com.enation.framework.util.StringUtil;

/**
 * @author kingapex
 *2015-4-1
 */
public class HelpPageCreator extends FacadePageParser {
	
	
	private int catid,articleid;
	
	public HelpPageCreator(int _catid,int _articleid){
		this.catid=_catid;
		this.articleid=_articleid;
	}
	

	@Override
	protected Writer getWriter() throws IOException{
		 String root_path = StringUtil.getRootPath();
		return new FileWriter(root_path+"/html/help-"+this.catid+"-"+articleid+".html");
	}
	
 

	@Override
	protected void outError(Exception e) {
		e.printStackTrace();
	}
	
}
