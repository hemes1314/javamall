package com.enation.app.b2b2c.component.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;
/**
 * 店铺商品搜索过滤器
 * @author LiFenLong
 *
 */
@Component
public class StoreSearchFilter extends AutoRegisterPlugin implements IGoodsSearchFilter{

	@Override
	public void createSelectorList(Map map,Cat cat){
	 
	}

	@Override
	public void filter(StringBuffer sql, Cat cat) {
		HttpServletRequest request= ThreadContextHolder.getHttpRequest();
		String store_id= request.getParameter("storeid");
		if (!StringUtil.isEmpty(store_id)) {
			sql.append(" and store_id="+store_id);
		}
	}

	 
}
