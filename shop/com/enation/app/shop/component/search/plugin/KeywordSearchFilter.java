package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.IPutWidgetParamsEvent;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 关键字搜索过滤器
 * @author kingapex
 *
 */
@Component
public class KeywordSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {
	

	@Override
	public void createSelectorList(Map mainmap ,Cat cat) {
 
	}
	
 
	public void filter(StringBuffer sql, Cat cat) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String keyword =  request.getParameter("keyword");
		if (!StringUtil.isEmpty(keyword)) {
			sql.append(" and ( g.name like '%");
			sql.append(keyword);
			sql.append("%')");
		}
	}
	
	public static void main(String [] args){
		String keyword = "测试的  123   0000   11   22";
		String[] keys  = keyword.split("\\s");
		for(String key:keys);
			//System.out.println(key);
	}
	
	
	public String getFilterId() {
		
		return "keyword";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "keywordSearchFilter";
	}

	
	public String getName() {
		
		return "关键字搜索过滤器";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}
	public void register() {
		

	}



}
