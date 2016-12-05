package com.gomecellar.workflow.service.impl;

import org.springframework.stereotype.Service;

import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.database.BaseSupport;
import com.gomecellar.workflow.service.IAppealRelationManager;

@Service
public class AppealRelationManager extends BaseSupport implements IAppealRelationManager {
	

	@Override
	public void refuse(String businessId ,String tradeno) {
		baseDaoSupport.execute("update es_order set status=? where sn=?",OrderStatus.ORDER_RETURN_REFUSE, businessId);
		baseDaoSupport.execute("update es_sellback_list set tradestatus=? where tradeno =?", 4 ,tradeno);

	}

	@Override
	public void comlete(String businessId) {
		baseDaoSupport.execute("update es_sellback_list set tradestatus=? ,tythstatus=1 where tradeno =?", 0,businessId);
		// 自营店更新订单状态为申请退货
		baseDaoSupport.execute("update es_order set status=? where sn = (select ordersn from es_sellback_list where tradeno = ?)", -3,businessId);
	}

}
