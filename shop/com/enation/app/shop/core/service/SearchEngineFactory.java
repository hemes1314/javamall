/**
 * 
 */
package com.enation.app.shop.core.service;

import com.enation.eop.SystemSetting;
import com.enation.framework.context.spring.SpringContextHolder;


/**
 * @author kingapex
 *2015-5-7
 */
public abstract class SearchEngineFactory {
	
	private SearchEngineFactory(){}
	
	public static IGoodsSearchManager getSearchEngine(){
		int lucene = SystemSetting.getLucene();
		if(lucene==0){
			return SpringContextHolder.getBean("goodsSearchManager");
		}else{
			return SpringContextHolder.getBean("goodsLuceneSearch");
		}
	}
}
