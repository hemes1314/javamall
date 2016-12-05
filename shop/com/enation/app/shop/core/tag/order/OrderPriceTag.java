package com.enation.app.shop.core.tag.order;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.impl.CartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单价格tag
 * @author kingapex
 *2013-7-26下午1:27:28
 */
@Component
@Scope("prototype")
public class OrderPriceTag extends BaseFreeMarkerTag {
    @Autowired
	private CartManager cartManager;
	private IMemberAddressManager memberAddressManager ;
	private CartPluginBundle cartPluginBundle;
	/**
	 * 订单价格标签
	 * @param address_id:收货地址id，int型
	 * @param shipping_id:配送方式id，int型
	 * @return 订单价格,OrderPrice型
	 * {@link OrderPrice}
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {
		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		String sessionid  = request.getSession().getId();
		
		Integer addressid =(Integer)args.get("address_id");
		Integer shipping_id =(Integer)args.get("shipping_id");

		//是否只计算购物车中选中的商品价格
		String onlySelected = (String)args.get("onlyselected");

		String regionid =null;
		
		//如果传递了地址，已经选完配送方式了
		if(addressid!=null){
			MemberAddress address =memberAddressManager.getAddress(addressid);
			regionid= ""+address.getRegion_id();
		}
		List<CartItem> cartList  = cartManager.listGoods(sessionid, onlySelected != null && onlySelected.equals("yes"));
		//计算订单价格
		OrderPrice orderprice  =this.cartManager.countPrice(cartList, shipping_id,regionid);
		
		//激发价格计算事件
		orderprice  = this.cartPluginBundle.coutPrice(orderprice);
		
		return orderprice;
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}

	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

}
