package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.utils.PriceUrlUtils;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 价格搜索过虑器<br/>
 * 由themes下加载 price_filter.xml 过滤条件
 *  
 * @author kingapex
 *
 */
@Component
public class PriceSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {

	private static Map<String,List<Price>> priceMap;
	
	public void createSelectorList(Map map,Cat cat){
		PriceUrlUtils.createAndPut(map);
	}

	
	public void filter(StringBuffer sql, Cat cat) { 
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		String urlFragment = request.getParameter("price");
		if(!StringUtil.isEmpty( urlFragment ) ){
			String[] price = urlFragment.split("_");
			if(price!=null && price.length>=1 && !StringUtil.isEmpty(price[0])){
				sql.append(" and  g.price>="+price[0]);				
			}
			if(price!=null && price.length>=2 && !StringUtil.isEmpty(price[1])){
				sql.append(" and g.price<"+price[1]);				
			}
			
		}
	}

	
	
	 
	
	public String getFilterId() {
		
		return "price";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "priceSearchFilter";
	}

	
	public String getName() {
		
		return "价格搜索过虑器";
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
