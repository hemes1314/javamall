package com.enation.app.shop.core.tag.order;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.impl.GoodsManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 订单货物列表标签
 * @author kingapex
 *2013-7-28下午3:54:32
 */
@Component
@Scope("prototype")
public class OrderItemListTag extends BaseFreeMarkerTag {
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;
	private IOrderManager orderManager;
	private IGoodsManager goodsManager;
	private IGoodsCatManager goodsCatManager;

	
	/**
	 * 订单货物列表标签
	 * @param orderid:订单id，int型
	 * @return 订单货物列表 ,List<OrderItem>型
	 * {@link OrderItem}
	 */
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer orderid  =(Integer)params.get("orderid");
		if(orderid==null){
			throw new TemplateModelException("必须传递orderid参数");
		}
		List<OrderItem> itemList  =orderManager.listGoodsItems(orderid);
		// add by lxl  商品删除或者下架，没有静态页 
		for (OrderItem item : itemList){

		    Goods goods = goodsManager.getGoods(item.getGoods_id());
		    if (goods == null ) {
		        item.setDisable(1);
	            item.setMarket_enable(0);
		    }else{
		        item.setDisable(goods.getDisabled());
	            item.setMarket_enable(goods.getMarket_enable());
		    }
		}
		//end
		return itemList;
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

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
