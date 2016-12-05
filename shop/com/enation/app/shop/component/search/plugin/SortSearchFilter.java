package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.IGoodsSortFilter;
import com.enation.app.shop.core.plugin.search.IPutWidgetParamsEvent;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.utils.SortUrlUtils;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.framework.plugin.AutoRegisterPlugin;

/***
 * 商品排序过滤器
 * @author kingapex
 *
 */
@Component
public class SortSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.search.IGoodsSearchFilter#createSelectorList(java.util.Map, com.enation.app.shop.core.model.Cat)
	 */
	@Override
	public void createSelectorList(Map map, Cat cat) {
		SortUrlUtils.createAndPut(map);
		
	}
 
	
	public String getId() {
		
		return "sortSearchFilter";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}








	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.search.IGoodsSearchFilter#filter(java.lang.StringBuffer, com.enation.app.shop.core.model.Cat)
	 */
	@Override
	public void filter(StringBuffer sql, Cat cat) {
		// TODO Auto-generated method stub
		
	}

 

}
