/**
 * 
 */
package com.enation.app.shop.component.goodsindex.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.facet.FacetResult;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodsindex.service.ISearchSelectorCreator;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.utils.ParamsUtils;
import com.enation.app.shop.core.utils.SortContainer;
import com.enation.app.shop.core.utils.SortUrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 排序选择器生成
 * @author kingapex
 *2015-4-23
 */
@Component
public class SortSelectorCreator implements ISearchSelectorCreator {
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.ISearchSelectorCreator#createAndPut(java.util.Map, java.util.List)
	 */
	@Override
	public void createAndPut(Map<String, Object> map, List<FacetResult> results) {
		SortUrlUtils.createAndPut(map);
	}
	
	
	
	 
	
	
	
}
