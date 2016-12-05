package com.enation.app.advbuy.core.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.advbuy.core.service.IAdvBuyManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * @ClassName: AdvBuyListTag 
 * @Description:  预售列表标签 
 * @author TALON 
 * @date 2015-7-31 上午10:49:00 
 *
 */
@Component
@Scope("prototype")
public class AdvBuyListTag extends BaseFreeMarkerTag {
	
	private IAdvBuyManager advBuyManager;
	/**
	 * 获取预售商品数据标签
	 * @param catid 预售分类Id
	 * @param minprice 最小价格
	 * @param maxprice 最大价格
	 * @param sort_key 排序类型
	 * @param sort_type 预售类型
	 * @param area_id 地区Id
	 * @param page 预售商品分页列表
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		Integer pageNo = NumberUtils.toInt(request.getParameter("page"), 1);
		Integer catid = NumberUtils.toInt(request.getParameter("catid"), 0);
		Integer sort_key = NumberUtils.toInt(request.getParameter("sort_key"), 0);
		Integer sort_type = NumberUtils.toInt(request.getParameter("sort_type"), 0);
		Double minprice = NumberUtils.toDouble(request.getParameter("minprice"), 0d);
		Double maxprice = NumberUtils.toDouble(request.getParameter("maxprice"), 0d);
		Integer area_id= NumberUtils.toInt(request.getParameter("area_id"), 0);
		
		Page page=this.advBuyManager.search(this.getPage(), this.getPageSize(), catid, minprice, maxprice, sort_key, sort_type,area_id, area_id);
		Map<String, Object> result=new HashMap<>();
		result.put("page", page);
		result.put("catid", catid);
		result.put("sort_key", sort_key);
		result.put("sort_type", sort_type);
		result.put("minprice", minprice);
		result.put("maxprice", maxprice);
		result.put("area_id", area_id);
		result.put("totalCount", page.getTotalCount());
		result.put("pageSize", 12);
		result.put("pageTotalCount", page.getTotalPageCount());
		return result;
	}
	
	public IAdvBuyManager getAdvBuyManager() {
		return advBuyManager;
	}
	public void setAdvBuyManager(IAdvBuyManager advBuyManager) {
		this.advBuyManager = advBuyManager;
	}
	
	

}
