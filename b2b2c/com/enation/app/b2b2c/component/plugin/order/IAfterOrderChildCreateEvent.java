package com.enation.app.b2b2c.component.plugin.order;

import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;



/**
 * b2b2c子订单创建完成事件
 * @author FengXingLong
 * 2015-07-21
 */
public interface IAfterOrderChildCreateEvent {
	
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void onAfterOrderChildCreate(Order order ,List<CartItem>   itemList,String sessionid);
	
	
}
