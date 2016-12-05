/**
 * 
 */
package com.enation.app.shop.component.goodsindex.service;

import java.util.List;
import java.util.Map;

import org.apache.lucene.facet.FacetResult;

/**
 * 
 * 搜索选择器生成接口
 * @author kingapex
 *2015-4-23
 */
public interface ISearchSelectorCreator {
	
	
	/**
	 * 生成选择器并压入指定的map
	 * @param map
	 */
	public void createAndPut(Map<String,Object>  map,List<FacetResult> results);
}
