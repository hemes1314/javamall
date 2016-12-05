package com.enation.app.b2b2c.core.tag.goods;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 商品搜索标签
 * @author LiFenLong
 *
 */
@Component
public class B2b2cGoodsSearchTag extends BaseFreeMarkerTag{
	private IStoreGoodsManager storeGoodsManager;
	private IGoodsCatManager goodsCatManager;
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		String namekeyword=request.getParameter("namekeyword");
		String cat_id=request.getParameter("cat_id");
		String pageNo = request.getParameter("page");
		String search_type=request.getParameter("search_type")==null?"0":request.getParameter("search_type");
		int pageSize=8;
		pageNo = (pageNo == null || pageNo.equals("")) ? "1" : pageNo;
		
		Map map=new HashMap();
		map.put("namekeyword", namekeyword);
		map.put("cat_id", cat_id);
		map.put("search_type", search_type);
		Page page = storeGoodsManager.b2b2cGoodsList(NumberUtils.toInt(pageNo), pageSize, map);
		
		Map result=new HashMap();
		result.put("goodsList", page);
		result.put("totalCount", page.getTotalCount());
		result.put("pageSize", pageSize);
		result.put("pageTotalCount", page.getTotalPageCount());
		if(!StringUtil.isEmpty(cat_id)){
			result.put("cat", goodsCatManager.getById(NumberUtils.toInt(cat_id)));
		}
		if(!StringUtil.isEmpty(namekeyword)){
			result.put("namekeyword", namekeyword);
		}
		result.put("search_type", search_type);
		result.put("currentPageNo", pageNo);
		return result;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
}
