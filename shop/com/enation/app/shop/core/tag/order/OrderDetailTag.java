package com.enation.app.shop.core.tag.order;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 订单详细标签 
 * @author kingapex
 *2013-7-28上午10:25:29
 */
@Component
@Scope("prototype")
public class OrderDetailTag extends BaseFreeMarkerTag {
	private IOrderManager orderManager;
	private IStoreOrderManager storeOrderManager;
	private IStoreMemberManager storeMemberManager;
	/**
	 * 
	 * 订单详细标签
	 * 必须传递orderid或ordersn参数
	 * @param orderid,订单id，int型
	 * @param ordersn,订单sn,String 型
	 * @return 订单详细 ，Order型
	 * {@link Order}
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {
		Integer orderid  =(Integer)args.get("orderid");
		String ordersn  =(String)args.get("ordersn");
		
		
		if(orderid==null && StringUtil.isEmpty(ordersn)){
			throw new TemplateModelException("必须传递orderid参数或ordersn参数");
		}
		
		StoreOrder returnOrder  =null;
		StoreOrder order  =null;
		HttpServletResponse response= ThreadContextHolder.getHttpResponse();
		String ctx = this.getRequest().getContextPath();
		if(orderid!=null){
			order = storeOrderManager.get(orderid);
		}else if( !StringUtil.isEmpty(ordersn)){
			order =	storeOrderManager.get(ordersn);
		}
		
		Member member = UserConext.getCurrentMember();
		
		StoreMember storeMember = storeMemberManager.getStoreMember();
		
        if(member == null)
        {
            try {
                //未登陆跳转到登录页面
                response.sendRedirect(ctx+"/store/login.html");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }else
        {
			//如果该订单不存在
			if (order == null) {
				try {
					//跳转到个人中心
					response.sendRedirect(ctx + "/member/member.html");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//如果订单不是当前用户
			if(order.getMember_id().compareTo(member.getMember_id()) == 0){
				returnOrder = order;
			}else{
				returnOrder = null;
				if(storeMember != null && order.getStore_id() != null && storeMember.getStore_id() != null && storeMember.getStore_id().compareTo(order.getStore_id()) == 0){
					returnOrder = order;
				}
			}

        }
		if(returnOrder==null){
			try {
				//如果不是该订单用户，跳转到个人中心
				response.sendRedirect(ctx+"/member/member.html");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		if (returnOrder != null && returnOrder.getParent_id() != null) {
			Order parentOrder = orderManager.get(returnOrder.getParent_id());
			returnOrder.setParentOrder(parentOrder);
		}
		return returnOrder;
		
	}
	
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
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
