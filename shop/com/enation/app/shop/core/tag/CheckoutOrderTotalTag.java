package com.enation.app.shop.core.tag;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 结算的订单金额标签
 * @author lina
 * 2014-2-21
 */
@Component
@Scope("prototype")
public class CheckoutOrderTotalTag extends BaseFreeMarkerTag {
	private ICartManager cartManager;
	
	/**
	 * 结算的订单金额
	 * @param regionId 地区
	 * @param typeId 配送方式
	 * @return orderPrice订单金额信息
	 * {@link OrderPrice}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		Integer regionId = (Integer) params.get("regionId");//地区id
		Integer typeId = (Integer) params.get("typeId"); //配送方式id
		OrderPrice  orderPrice  = new OrderPrice();
		if(regionId!=null && typeId!=null)
			orderPrice  = this.cartManager.countPrice(cartManager.listGoods(sessionid), typeId, regionId.toString());
		return orderPrice;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
}
