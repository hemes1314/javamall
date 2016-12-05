package com.enation.app.b2b2c.core.service.order.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IB2B2cOrderReportManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentLogType;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class B2B2cOrderReportManager extends BaseSupport implements IB2B2cOrderReportManager {

	@Override
	public Page listPayment(Map map, int pageNo, int pageSize, String order) {
		String sql = createTempSql(map);
		Page webPage = this.daoSupport.queryForPage(sql, pageNo, pageSize);

		//统一获取支付方式,之前的sql太复杂了,一次性获取有难度,改为分两次, 一次性找出所有的父订单,统计支付方式放入map,然后倒腾回子订单map
		if (webPage.getResult() != null) {
			List list = (List)webPage.getResult();
			if (!list.isEmpty()) {
				Set<Integer> pidSet = new HashSet<>();
				Set<Integer> orderIdSet = new HashSet<>();
				for (Object o : list) {
					Map m = (Map) o;
					//获取订单id集合 由于封装的查询sql语句在子查询的时候会有个别字段不返回值 chenzhongwei add
                    Integer orderId = (Integer) m.get("order_id");
                    String orderIdSql = "select * from es_order where order_id = "+orderId;
                    List<Order> parentOrderId = baseDaoSupport.queryForList(orderIdSql, Order.class);
                    if(CollectionUtils.isNotEmpty(parentOrderId)){
                        m.put("parent_id", parentOrderId.get(0).getParent_id());
                    }
                    orderIdSet.add(orderId);
					Integer v = (Integer)m.get("parent_id");
					if (v != null) {
						pidSet.add(v);
					}
				}
				Integer[] array = new Integer[pidSet.size()];
				int i = 0;
				for (Integer pid : pidSet) {
					array[i++] = pid;
				}
				String ids = StringUtil.arrayToString(array, ",");
				//String sql1 = "select order_id,need_pay_money,payment_name,advance_pay,bonus_pay where order_id in (" + ids + ")";
				String sql1 = "select * from es_order where order_id in (" + ids + ") order by create_time desc";
				List<Order> parentOrders = baseDaoSupport.queryForList(sql1, Order.class);
				//添加主订单号 chenzhongwei add
				Map<Integer, String> parentSnMap = new HashMap<Integer, String>();
				Map<Integer, String> paymentMethodMap = new HashedMap();
				for (Order po : parentOrders) {
					parentSnMap.put(po.getOrder_id(), po.getSn());
					paymentMethodMap.put(po.getOrder_id(), po.getPaymentMethod());
				}
				//获取订单id集合 由于封装的查询sql语句在子查询的时候会有个别字段不返回值 chenzhongwei add
                Integer[] orderIdArray = new Integer[orderIdSet.size()];
                int j = 0;
                for (Integer orderId : orderIdSet) {
                    orderIdArray[j++] = orderId;
                }
                String orderIds = StringUtil.arrayToString(orderIdArray, ",");
                String orderIdSql = "select * from es_order where order_id in (" + orderIds + ")";
                List<Order> orderIdList = baseDaoSupport.queryForList(orderIdSql, Order.class);
                Map<Integer, Order> orderMap = new HashMap<Integer, Order>();
                for (Order orderInfo : orderIdList) {
                    orderMap.put(orderInfo.getOrder_id(), orderInfo);
                }
				for (Object o : list) {
					Map m = (Map) o;
					Integer v = (Integer)m.get("parent_id");
					if (v != null) {
						//m.put("paymentMethod", paymentMethodMap.get(v));
						//直接放入这里,不用修改页面了
						m.put("parent_sn", parentSnMap.get(v));
						m.put("pay_method", paymentMethodMap.get(v));
					}
					//获取订单id集合 由于封装的查询sql语句在子查询的时候会有个别字段不返回值 chenzhongwei add
					Integer orderId = (Integer)m.get("order_id");
                    Order orderTemp = orderMap.get(orderId);
                    m.put("store_name", orderTemp.getStore_name());
					m.put("order_amount", orderTemp.getOrder_amount());
                    m.put("bonus_pay", orderTemp.getBonus_pay());
                    m.put("advance_pay", orderTemp.getAdvance_pay());
                    m.put("shipping_amount", orderTemp.getShipping_amount());
                    m.put("tradeno", orderTemp.getTradeno());
				}

			}
		}

		return webPage;
	}

	@SuppressWarnings("unused")
	private String  createTempSql(Map map){
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer payment_id = (Integer) map.get("payment_id");
		
		String sql = "select l.*,o.store_name as store_name,o.parent_id as parent_id,o.order_amount,o.bonus_pay,o.advance_pay,o.shipping_amount,o.tradeno from es_payment_logs l INNER JOIN es_order o ON o.sn=l.order_sn  where o.parent_id is NOT NULL  and l.payment_id>0 and l.type="+PaymentLogType.receivable.getValue();
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
			    //防止查出多余数据 chenzhogwei update
                if(StringUtils.isNotEmpty(keyword)) {
                    sql += " and (o.sn like '%" + keyword + "%'";
                    sql += " or o.ship_name like '%" + keyword + "%')";
                }
			}
		}
		
		if(sn!=null && !StringUtil.isEmpty(sn)){
			sql+=" and o.order_sn like '%"+sn+"%'";
		}
		
		if(paystatus!=null){
			//sql+=" and o.status="+paystatus;
		    sql+=" and l.status="+paystatus; //2015/10/23 humaodong
		}
		
		if(payment_id!=null){
			sql+=" and o.order_id in(select order_id from es_order where payment_id="+payment_id+")";
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			sql+=" and o.create_time>="+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and o.create_time<="+etime;
		}
		
		sql += " order by pay_date desc";
		return sql;
	}
	@Override
	public Page listRefund(int pageNo, int pageSize, String order,Map map) {
		
		String keyword = (String) map.get("keyword");
		//chenzhongwei add
		// t2.advance_pay 余额
		// t2.need_pay_money 在线支付
		// t2.refund_batchno 退款流水号
		// t2.tradeno pay_tradeno 交易流水号
		// t2.refund_status 等于1的时候退款成功状态
		StringBuffer sql =new StringBuffer("select t1.*,t3.sn parent_ordersn,t4.sn refund_sn,t4.pay_date,t2.tradeno pay_tradeno,t2.advance_pay,t2.need_pay_money,t2.refund_batchno,t2.refund_status from es_sellback_list t1");
		sql.append(" INNER JOIN es_order t2 ON t1.orderid=t2.order_id");
		sql.append(" INNER JOIN es_order t3 ON t2.parent_id=t3.order_id");
		sql.append(" LEFT JOIN es_refund_logs t4 ON t4.order_id=t1.orderid");
		//判断关键字是否为空
		if(!StringUtil.isEmpty(keyword)){
			sql.append(" WHERE t1.tradeno like '%"+keyword+"%' or t1.sndto like '%"+keyword+"%'");
		}
		sql.append(" order by t1.id desc ");
		Page webpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		//添加扣款金额
        if(webpage.getResult() != null) {
            List list = (List) webpage.getResult();
            if(!list.isEmpty()) {
                DecimalFormat df = new DecimalFormat("0.00");
                for(Object o : list) {
                    Map m = (Map) o;
                    BigDecimal advance_pay = new BigDecimal(m.get("advance_pay").toString());
                    BigDecimal need_pay_money = new BigDecimal(m.get("need_pay_money").toString());
                    BigDecimal return_price = new BigDecimal(m.get("return_price").toString());
                    BigDecimal chargebacks = BigDecimal.ZERO;
                    chargebacks=advance_pay.add(need_pay_money).subtract(return_price);
                    m.put("chargebacks",df.format(chargebacks));
                    //退款单列表交易流水号是否显示 chenzhongwei add
                    Integer tradestatus = (Integer) m.get("tradestatus");
                    if(tradestatus != 3) {
                        m.put("pay_tradeno", "");
                    }
                    if (need_pay_money.compareTo(BigDecimal.ZERO) > 0) {
                        if (return_price.compareTo(need_pay_money) > 0) {
                            BigDecimal alltotal_advance_pay = return_price.subtract(need_pay_money);
                            if (alltotal_advance_pay.compareTo(advance_pay) < 0) {
                                advance_pay = alltotal_advance_pay;
                            }
                        } else {
                            advance_pay = BigDecimal.ZERO;
                        }
                    } else {
                        // 如果 申请退款金额 大于 余额支付金额 退款金额为 余额支付金额 否则为 申请退款金额
                        if (return_price.compareTo(advance_pay) <= 0) {
                            advance_pay = return_price;
                        }
                    }
                    //退款余额 chenzhongwei add
                    BigDecimal return_advance_pay = BigDecimal.ZERO;
                    m.put("return_advance_pay",df.format(advance_pay));
                }
            }
        }
		return webpage;
	}

}
