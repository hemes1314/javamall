package com.enation.app.shop.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 会员订单列表标签
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberOrderListTag extends BaseFreeMarkerTag{

	private IMemberOrderManager memberOrderManager;
	
	private IOrderManager orderManager;
	
	private IPromotionManager promotionManager;
	
	private IOrderFlowManager orderFlowManager;
	private IMemberPointManger memberPointManger;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		String status = request.getParameter("status");
		String keyword = request.getParameter("keyword");
		
		Page ordersPage = memberOrderManager.pageOrders(Integer.valueOf(page), pageSize,status,keyword);
		Long totalCount = ordersPage.getTotalCount();
		
		List ordersList = (List) ordersPage.getResult();
		ordersList = ordersList == null ? new ArrayList() : ordersList;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("ordersList", ordersList);

		//Author LiFenLong
		Map<String,Object> orderstatusMap=OrderStatus.getOrderStatusMap();
		for (String orderStatus: orderstatusMap.keySet()) {
			result.put(orderStatus, orderstatusMap.get(orderStatus));
		}
		
		if(status!=null){
			result.put("status",Integer.valueOf( status) );
		}
		
		return result;
	}

	public IMemberOrderManager getMemberOrderManager() {
		return memberOrderManager;
	}
	public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
		this.memberOrderManager = memberOrderManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}
	
	

}
