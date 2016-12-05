package com.enation.app.shop.component.ordercore.plugin.timeout;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 不付款的订单24小时内就取消
 * @author LiFenLong
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class TimeOutOrderPrint extends AutoRegisterPlugin implements IEveryHourExecuteEvent, IEveryMinutesExecuteEvent {
	private IDaoSupport daoSupport;
	private IOrderFlowManager orderFlowManager;
	
	@Override
	public void everyHour() {
//		String sql="SELECT order_id from es_order  WHERE disabled=0 AND (status=? or status=?) AND create_time between ? AND ?";
//		List<Map> list = daoSupport.queryForList(sql,
//				OrderStatus.ORDER_NOT_PAY, OrderStatus.ORDER_NOT_CONFIRM,
//				1398873600, DateUtil.getDateline() - 24 * 60 * 60);
//		if (list == null) return;
//		Integer orderId = null;
//		for (Map map : list) {
//			orderId = NumberUtils.toInt(map.get("order_id").toString());
//			logger.info("订单自动取消：order_id = " + orderId);
//			orderFlowManager.cancel(orderId, "订单24小时没有进行付款");
//		}
	}
	
	/**
	 * 测试用，临时将不付款订单取消时间的定时任务从每天执行修改为每分钟执行.
	 */
	@Override
	public void everyMinutes() {
		String sql="SELECT order_id from es_order  WHERE disabled=0 AND (status=? or status=?) AND create_time between ? AND ?";
		List<Map> list = daoSupport.queryForList(sql,
				OrderStatus.ORDER_NOT_PAY, OrderStatus.ORDER_NOT_CONFIRM,
				1398873600, DateUtil.getDateline() - 10 * 60);
		if (list == null) return;
		Integer orderId = null;
		for (Map map : list) {
			orderId = NumberUtils.toInt(map.get("order_id").toString());
			logger.info("订单自动取消：order_id = " + orderId);
			orderFlowManager.cancel(orderId, "[测试]订单10分钟没有进行库款");
		}
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}
	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}
}
