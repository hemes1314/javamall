package com.enation.app.b2b2c.core.service.order.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.OrderLogi;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


/**
 * 订单物流状态更新
 * @author add by lin
 * 2015年10月13日
 */
@Component
public class OrderLogiManager extends BaseSupport {
	
	
	private OrderLogi orderLogi;
	
	public void addUpdateOrderLogi(OrderLogi orderLogi)
	{
        String sql = "select * from es_order_logi where key_id="+orderLogi.getKey_id()+" and com="+orderLogi.getCom()+" and logi_num="+orderLogi.getLogi_num();
        OrderLogi tempObj = (OrderLogi) baseDaoSupport.queryForObject(sql,OrderLogi.class);
	    if(tempObj == null){
	     this.baseDaoSupport.insert("es_order_logi", orderLogi);
	    }else
	    {
	        updateOrderLogi(orderLogi);  
	    }
	}
	
	public void updateOrderLogi(OrderLogi orderLogi)
	{
	    baseDaoSupport.execute("update es_order_logi set detail=? where key_id=? and com =? and logi_num=?", orderLogi.getDetail(),orderLogi.getKey_id(),orderLogi.getCom(),orderLogi.getLogi_num());
	}
	
    public void getOrderLogi(String keyid,String com,String logiNum)
    {
        String sql = "select * from es_order_logi where key_id="+keyid+" and com="+com+" and logi_num="+logiNum;
        baseDaoSupport.queryForObject(sql,OrderLogi.class);
    }

}

