package com.enation.app.shop.component.member.plugin.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.plugin.member.IMemberTabShowEvent;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.StringUtil;

/**
 * 会员订单列表插件
 * @author kingapex
 *2012-4-1下午3:21:22
 */
@Component
public class MemberOrderListPlugin extends AutoRegisterPlugin implements IMemberTabShowEvent,
 IAjaxExecuteEnable{

	private IOrderManager orderManager;
	
	/**
	 * 响应插件异步执行接口
	 * 异步请求会走这里
	 */
	@Override
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int memberid  = StringUtil.toInt(request.getParameter("memberid"),true);
		
		//查询这个会员的订单列表
		List orderList = this.orderManager.listOrderByMemberId(memberid);
		
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("orderList",orderList);
		freeMarkerPaser.setPageName("order_list");		
		return freeMarkerPaser.proessPageContent();
	}

	
	/**
	 * 响应会员详细信息显示事件，先显示出一个容器，然后在点击相应的tab页时加载订单列表。
	 * 这样做的目的是为了避免在显示会员详细信息时一次性多过的查询数据库（通过tab页切换时再加载一次方式）。
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		Map statusMap = null;
		String status_Json=null;
		if(statusMap==null){
			statusMap = new HashMap();
			statusMap = getStatusJson();
			String p= JSONArray.fromObject(statusMap).toString();
			status_Json=p.replace("[", "").replace("]", "");
		}
		freeMarkerPaser.putData("memberid",member.getMember_id());
		freeMarkerPaser.putData("status_Json",status_Json);
	 
		freeMarkerPaser.setPageName("member_order");		
		return freeMarkerPaser.proessPageContent();
	}
	
	/**
	 * 获取订单状态的json
	 * @return
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));// 未付款/新订单       修改为已确认
        orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));//订单已生效
        orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));// 已确认支付
        orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));//配货完成，待发货
        orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));// 已发货
        orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));// 已收货
        orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));// 退货
        orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));// 已完成
        orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));// 退款
        orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));// 作废
        orderStatus.put(""+OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));//已换货
        orderStatus.put(""+OrderStatus.ORDER_CHANGE_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));//申请换货
        orderStatus.put(""+OrderStatus.ORDER_RETURN_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));// 申请退货
        orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));// 已支付待确认
        orderStatus.put(""+OrderStatus.ORDER_RETURNED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURNED));//已退货
        orderStatus.put(""+OrderStatus.ORDER_CHANGE_REFUSE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_REFUSE));//换货被拒绝
        orderStatus.put(""+OrderStatus.ORDER_RETURN_REFUSE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_REFUSE));//退货被拒绝
        orderStatus.put(""+OrderStatus.ORDER_ALLOCATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION));//配货中
		return orderStatus;
	}

	@Override
	public String getTabName(Member member) {
		
		return "他的订单";
	}

	@Override
	public int getOrder() {
		
		return 5;
	}

	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}


	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	
}
