package com.enation.app.b2b2c.core.tag.goods;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺商品标签
 * @author LiFenLong
 *
 */
@Component
public class MyGoodsListTag extends BaseFreeMarkerTag{
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		//session中获取会员信息,判断用户是否登陆
		StoreMember member=storeMemberManager.getStoreMember();
		if(member==null){
			HttpServletResponse response= ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect("login.html");
			} catch (IOException e) {
				throw new UrlNotFoundException();
			}
		}
		Map<String, Object> result = new HashMap<>();
		int pageSize=10;
		Integer pageNo = NumberUtils.toInt(request.getParameter("page"), 1);
		result.put("store_id", member.getStore_id());
		result.put("disable", NumberUtils.toInt(request.getParameter("disable"), 0));
		result.put("store_cat", request.getParameter("store_cat"));
		result.put("goodsName", request.getParameter("goodsName"));
		result.put("market_enable", NumberUtils.toInt(request.getParameter("market_enable"), -1));
		Page storegoods= storeGoodsManager.storeGoodsListDels(pageNo, pageSize, result);
		result.put("page", pageNo);
		result.put("pageSize", pageSize);
		result.put("totalCount", storegoods.getTotalCount());
		result.put("storegoods", storegoods);
		return result;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
