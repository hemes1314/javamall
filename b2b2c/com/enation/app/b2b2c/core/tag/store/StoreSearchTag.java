package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 店铺列表标签
 * @author LiFenLong
 *
 */
public class StoreSearchTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	@SuppressWarnings("rawtypes")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		Map<String, Object> map = new HashMap<>();
		map.put("name", request.getParameter("keyword"));
		map.put("store_credit", request.getParameter("store_credit"));
		map.put("searchType", request.getParameter("searchType"));
		int pageSize=10;
		Integer pageNo = NumberUtils.toInt(request.getParameter("page"), 1);
		Page page=storeManager.store_list(map,1, pageNo, pageSize);
		map.put("list", page);
		map.put("totalCount", page.getTotalCount());
		map.put("pageSize", pageSize);
		map.put("page", pageNo);
		return map; 
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
