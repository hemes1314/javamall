package com.enation.app.shop.core.action.backend;

import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.action.WWAction;

/**
 * 后台首页显示项
 * @author kingapex
 * 2010-10-12上午10:18:15
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("indexItem")
@Results({
	@Result(name="order", type="freemarker", location="/shop/admin/index/order.html"),
	@Result(name="goods", type="freemarker", location="/shop/admin/index/goods.html")
})
public class ShopIndexItemAction extends WWAction {
	
	private IOrderManager orderManager;
	private IGoodsManager goodsManager;
	private Map orderss; //订单统计信息
	private Map goodsss ;//商品统计信息
	
	/**
	 * 统计订单状态
	 * @param orderss订单状态,Map
	 * @return 订单统计页
	 */
	public String order(){
		this.orderss  =this.orderManager.censusState();
		return "order";
	}
	
	
	/**
	 * 商品统计信息
	 * @param goodsss 商品统计信息
	 * @return 商品统计页
	 */
	public String goods(){
		this.goodsss = this.goodsManager.census();
		return "goods";
	}


	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}


	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}


	public Map getOrderss() {
		return orderss;
	}


	public void setOrderss(Map orderss) {
		this.orderss = orderss;
	}


	public Map getGoodsss() {
		return goodsss;
	}


	public void setGoodsss(Map goodsss) {
		this.goodsss = goodsss;
	}
}
