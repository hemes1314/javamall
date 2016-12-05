package com.enation.app.base.core.service.solution.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.enation.app.base.core.service.solution.IProfileLoader;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.util.StringUtil;

public class ProfileLoaderImpl implements IProfileLoader {

	protected final Logger logger = Logger.getLogger(getClass());
	public Document load(String productId) {
		String app_apth = StringUtil.getRootPath();

		String xmlFile = app_apth+"/products/"+productId +"/profile.xml"; 
		try {
		    DocumentBuilderFactory factory = 
		    DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document document = builder.parse(xmlFile);
		    return document;
		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException("load ["+productId +"] profile error" );
		} 	 
		 
	}

}
