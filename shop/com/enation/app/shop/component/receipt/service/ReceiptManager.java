package com.enation.app.shop.component.receipt.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.receipt.Receipt;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;


/**
 * 发票管理
 * @author kingapex
 *2012-2-6下午10:25:55
 */
@Component
public class ReceiptManager extends BaseSupport<Receipt> implements IReceiptManager {

	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(Receipt invoice) {
		invoice.setAdd_time( System.currentTimeMillis() );
		this.baseDaoSupport.insert("receipt", invoice);
		//this.baseDaoSupport.execute("update order set apply_invoice=1 where order_id=?", invoice.getOrder_id());//改写订单表索取发票字段
	}

	@Override
	public Receipt getByOrderid(Integer orderid) {
 
		List list= this.baseDaoSupport.queryForList("select * from receipt where order_id = ? ",Receipt.class,orderid);
		if(list.size()==0){
			return null;
		}else{
			return (Receipt)list.get(0);
		}
	}


}
