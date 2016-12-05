package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.eop.sdk.database.BaseSupport;

public class OrderMetaManager extends BaseSupport<OrderMeta> implements
		IOrderMetaManager {

	@Override
	public void add(OrderMeta orderMeta) {
		this.baseDaoSupport.insert("order_meta", orderMeta) ;
	}

	
	
	@Override
	public List<OrderMeta> list(int orderid) {
		 
		return this.baseDaoSupport.queryForList("select * from order_meta where orderid=?", OrderMeta.class,orderid);
	}
	
	

	@Override
	public OrderMeta get(int orderid, String meta_key) {
		return this.baseDaoSupport.queryForObject("select * from order_meta where orderid=? and meta_key=?", OrderMeta.class,orderid,meta_key);
	}

}
