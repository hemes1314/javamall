package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 我的店铺其他信息Tag
 * @author LiFenLong
 *
 */
public class MyStoreDetailOtherTag extends BaseFreeMarkerTag{
	private IStoreOrderManager storeOrderManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberCommentManager storeMemberCommentManager;
	private IStoreMemberManager storeMemberManager;
	private Integer store_id;
	
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result=new HashMap();
		//店铺订单数量
		int storeAllOrder= storeOrderManager.getStoreOrderNum(-999);	//店铺全部订单
		int orderNotPay=storeOrderManager.getStoreOrderNum(0);			//未付款订单
		int orderPay=storeOrderManager.getStoreOrderNum(1);				//等待收款
		int orderNotShip=storeOrderManager.getStoreOrderNum(2);			//等待发货
		int orderNotRog=storeOrderManager.getStoreOrderNum(5);			//等待收货
		int orderComplete=storeOrderManager.getStoreOrderNum(7);		//订单已完成

		//店铺仓库中商品数量
		int notMarket=storeGoodsManager.getStoreGoodsNum(0);
		
		//店铺中出售中的商品数量
		int ingMarket=storeGoodsManager.getStoreGoodsNum(1);
		
		//卖家未处理得商品留言
		StoreMember member=storeMemberManager.getStoreMember();
		int notReply=storeMemberCommentManager.getCommentCount(2,member.getStore_id());
		
		result.put("storeAllOrder", storeAllOrder);
		result.put("orderNotPay", orderNotPay);
		result.put("orderPay", orderPay);
		result.put("orderNotShip", orderNotShip);
		result.put("orderNotRog", orderNotRog);
		result.put("orderComplete", orderComplete);
		
		result.put("notMarket", notMarket);
		result.put("notReply", notReply);
		result.put("ingMarket", ingMarket);
		return result;
	}
	
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public IStoreMemberCommentManager getStoreMemberCommentManager() {
		return storeMemberCommentManager;
	}

	public void setStoreMemberCommentManager(
			IStoreMemberCommentManager storeMemberCommentManager) {
		this.storeMemberCommentManager = storeMemberCommentManager;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
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
