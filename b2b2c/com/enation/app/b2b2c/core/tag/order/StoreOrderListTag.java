package com.enation.app.b2b2c.core.tag.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 获取店铺订单
 * @author LiFenLong
 *
 */
@Component
public class StoreOrderListTag extends BaseFreeMarkerTag{
	private IStoreOrderManager storeOrderManager;
	private IStoreMemberManager storeMemberManager;
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
		//获取订单列表参数
		int pageSize=10;
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		String order_state = request.getParameter("order_state");
		String keyword=request.getParameter("keyword");
		String buyerName=request.getParameter("buyerName");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		
 
		
		Map result=new HashMap();
		result.put("keyword", keyword);
		result.put("order_state", order_state);
		result.put("buyerName", buyerName);
		result.put("startTime", startTime);
		result.put("endTime", endTime);

		Page orderList = storeOrderManager.storeOrderList(NumberUtils.toInt(page), pageSize, member.getStore_id(), result);
		//获取总记录数
		Long totalCount = orderList.getTotalCount();
		
		result.put("page", page);
		result.put("pageSize", pageSize);
		result.put("totalCount", totalCount);
		result.put("storeOrder", orderList);
		return result;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
