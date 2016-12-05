package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.RefundLog;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * 订单单据管理
 * @author lzf<br/>
 * 2010-4-12下午12:17:49<br/>
 * version 1.0
 * 
 * version 2.0- by kingapex ,2012-03-09
 * 收款单、退款单表拆分、字段更改。
 */
public class OrderReportAction extends WWAction {
	
	private IOrderReportManager orderReportManager;
	
	private IOrderMetaManager orderMetaManager;
	private IMemberManager memberManager;
	private IAdminUserManager adminUserManager;
	private ISellBackManager sellBackManager;
	private IOrderManager orderManager;
	private ILogiManager logiManager;
	
	private String order;
	private Order ret_order;
	private int id;
	private PaymentLog payment;
	private RefundLog refund;
	private Delivery delivery;
	private List<DeliveryItem> listDeliveryItem;
	private IPaymentManager paymentManager;
	private List paymentList;	
	private IOrderFlowManager orderFlowManager;
	private int orderId;
	
	private List paymentLogsList;
	private List refundLogsList;
	private SellBackList sellBackList;
	private List goodsList;
	private List metaList;
	private List logList;
	private Integer is_all;
	private Integer sid;
	
	private Map statusMap;
	private Map payStatusMap;
	private Map shipMap;
	private String status_Json;
	private String payStatus_Json;
	private String ship_Json;
	private IDlyTypeManager dlyTypeManager;
	private List<DlyType> shipTypeList;
	private List<PayCfg> payTypeList;
	private Map orderMap;
	private Integer stype;
	private String keyword;
	private String start_time;
	private String end_time;
	private String sn;
	private Integer paystatus=null;
	private Integer payment_id=null;
	/**
	 * 付款单列表
	 * @param statusMap 订单状态,Map
	 * @param payStatusMap 付款状态,Map
	 * @param shipMap 发货状态,Map
	 * @param shipTypeList 配送方式列表,List
	 * @param payTypeList 付款方式列表,List
	 * @return
	 */
	public String paymentList(){
		if(statusMap==null){
			statusMap = new HashMap();
			statusMap = getStatusJson();
			String p= JSONArray.fromObject(statusMap).toString();
			status_Json=p.replace("[", "").replace("]", "");
		}
		if(payStatusMap==null){
			payStatusMap = new HashMap();
			payStatusMap = getpPayStatusJson();
			String p= JSONArray.fromObject(payStatusMap).toString();
			payStatus_Json=p.replace("[", "").replace("]", "");
			
		}
		if(shipMap ==null){
			shipMap = new HashMap();
			shipMap = getShipJson();
			String p= JSONArray.fromObject(shipMap).toString();
			ship_Json = p.replace("[", "").replace("]", "");
		}
		shipTypeList = dlyTypeManager.list();
		payTypeList = paymentManager.list();
		return "paymentList";
	}
	/**
	 * 获取付款单列表Json
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param start_time 下单时间,String
	 * @param end_time 结束时间,Stirng
	 * @param sn 编号,String
	 * @param paystatus 付款状态,Integer
	 * @param payment_id 付款方式Id,Integer
	 * @param order 排序
	 * @return 付款单列表Json
	 */
	public String paymentListJson(){
		orderMap = new HashMap();
		orderMap.put("stype", stype);
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("sn", sn);
		orderMap.put("paystatus", paystatus);
		orderMap.put("payment_id", payment_id);
		this.webpage = orderReportManager.listPayment(orderMap,this.getPage(), this.getPageSize(), order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String paymentLogs(){
		payment = orderReportManager.getPayment(id);
		paymentLogsList = orderReportManager.listPayLogs(payment.getOrder_id());
		return "paymentLogs";
	}
	/**
	 * 付款单详细
	 * @param id 付款单Id,Integer
	 * @param payment 付款方式,PaymentLog
	 * @return 付款单详细页
	 */
	public String paymentDetail(){
		payment = orderReportManager.getPayment(id);
		this.paymentList  = this.paymentManager.list();
		return "paymentDetail";
	}
	
	public String refundList(){
		return "refundList";
	}
	public String refundListJson(){
		this.webpage = orderReportManager.listRefund(this.getPage(), this.getPageSize(), order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String refundLogs(){
		payment = orderReportManager.getPayment(id);
		refundLogsList = orderReportManager.listRefundLogs(payment.getOrder_id());
		return "refundLogs";
	}
	
	public String refundDetail(){
		refund = orderReportManager.getRefund(id);
		return "refundDetail";
	}
	
	
	/**
	 * 读取配货单列表
	 * @return
	 */
	public String allocationList(){
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String depotid = request.getParameter("depotid");
		if(depotid==null){
			depotid="0";
		}
		this.webpage = orderReportManager.listAllocation(NumberUtils.toInt(depotid), 0, this.getPage(), this.getPageSize());
		return  "allocationList";
	}
	/**
	 * 读取配货单列表
	 * @return
	 */
	public String allocationedList(){
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String depotid = request.getParameter("depotid");
		if(depotid==null){
			depotid="0";
		}
		this.webpage = orderReportManager.listAllocation(NumberUtils.toInt(depotid), 1, this.getPage(), this.getPageSize());
		return  "allocationList";
	}
	
	
	
	public String shippingList(){
		return "shippingList";
	}
	public String shippingListJson(){
		this.webpage = orderReportManager.listShipping(this.getPage(), this.getPageSize(), order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String shippingDetail(){
		delivery = orderReportManager.getDelivery(id);
		listDeliveryItem = orderReportManager.listDeliveryItem(id);
		return "shippingDetail";
	}
	
	//退货单
	public String returnedList(){
		return "returnedList";
	}
	public String returnedListJson(){
		this.webpage = orderReportManager.listReturned(this.getPage(), this.getPageSize(), null);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 退货入库
	 * @return
	 */
	public String returned(){
		sellBackList = this.sellBackManager.get(sid);//退货详细
		ret_order = orderManager.get(sellBackList.getOrdersn());//订单详细
		goodsList = this.sellBackManager.getGoodsList(sid,sellBackList.getOrdersn());//退货商品列表
		logList = this.sellBackManager.sellBackLogList(sid);//退货操作日志
		metaList = orderMetaManager.list(ret_order.getOrder_id());//订单的使用的积分、余额
		return "returned";
	}	
	
	public String returnedDetail(){
		delivery = orderReportManager.getDelivery(id);
		listDeliveryItem = orderReportManager.listDeliveryItem(id);
		return "returnedDetail";
	}

	
	/**
	 * 获取订单状态的json
	 * @return
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		//orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));

		return orderStatus;
	}
	
	
	private Map getpPayStatusJson(){
		
		Map pmap = new HashMap();
		pmap.put(""+OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
	//	pmap.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
		pmap.put(""+OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
		pmap.put(""+OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
		pmap.put(""+OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));

		return pmap;
	}
	
	private Map getShipJson(){
		Map map = new HashMap();
		map.put(""+OrderStatus.SHIP_ALLOCATION_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_NO));
		map.put(""+OrderStatus.SHIP_ALLOCATION_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_YES));
		map.put(""+OrderStatus.SHIP_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_NO));
		map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put(""+OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
		map.put(""+OrderStatus.SHIP_PARTIAL_SHIPED, OrderStatus.getShipStatusText(OrderStatus.SHIP_PARTIAL_SHIPED));
		map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put(""+OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
		map.put(""+OrderStatus.SHIP_CHANED, OrderStatus.getShipStatusText(OrderStatus.SHIP_CHANED));
		map.put(""+OrderStatus.SHIP_ROG, OrderStatus.getShipStatusText(OrderStatus.SHIP_ROG));
		return map;
	}
	

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}

	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PaymentLog getPayment() {
		return payment;
	}

	public void setPayment(PaymentLog payment) {
		this.payment = payment;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public List<DeliveryItem> getListDeliveryItem() {
		return listDeliveryItem;
	}

	public void setListDeliveryItem(List<DeliveryItem> listDeliveryItem) {
		this.listDeliveryItem = listDeliveryItem;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public List getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List paymentList) {
		this.paymentList = paymentList;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public List getPaymentLogsList() {
		return paymentLogsList;
	}

	public void setPaymentLogsList(List paymentLogsList) {
		this.paymentLogsList = paymentLogsList;
	}

	public List getRefundLogsList() {
		return refundLogsList;
	}

	public void setRefundLogsList(List refundLogsList) {
		this.refundLogsList = refundLogsList;
	}

	public RefundLog getRefund() {
		return refund;
	}

	public void setRefund(RefundLog refund) {
		this.refund = refund;
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}
	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public ILogiManager getLogiManager() {
		return logiManager;
	}
	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}
	public SellBackList getSellBackList() {
		return sellBackList;
	}
	public void setSellBackList(SellBackList sellBackList) {
		this.sellBackList = sellBackList;
	}
	public List getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List goodsList) {
		this.goodsList = goodsList;
	}
	public List getMetaList() {
		return metaList;
	}
	public void setMetaList(List metaList) {
		this.metaList = metaList;
	}
	public List getLogList() {
		return logList;
	}
	public void setLogList(List logList) {
		this.logList = logList;
	}
	public Integer getIs_all() {
		return is_all;
	}
	public void setIs_all(Integer is_all) {
		this.is_all = is_all;
	}
	public Order getRet_order() {
		return ret_order;
	}
	public void setRet_order(Order ret_order) {
		this.ret_order = ret_order;
	}
	public Integer getSid() {
		return sid;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	public Map getStatusMap() {
		return statusMap;
	}
	public void setStatusMap(Map statusMap) {
		this.statusMap = statusMap;
	}
	public Map getPayStatusMap() {
		return payStatusMap;
	}
	public void setPayStatusMap(Map payStatusMap) {
		this.payStatusMap = payStatusMap;
	}
	public Map getShipMap() {
		return shipMap;
	}
	public void setShipMap(Map shipMap) {
		this.shipMap = shipMap;
	}
	public String getStatus_Json() {
		return status_Json;
	}
	public void setStatus_Json(String status_Json) {
		this.status_Json = status_Json;
	}
	public String getPayStatus_Json() {
		return payStatus_Json;
	}
	public void setPayStatus_Json(String payStatus_Json) {
		this.payStatus_Json = payStatus_Json;
	}
	public String getShip_Json() {
		return ship_Json;
	}
	public void setShip_Json(String ship_Json) {
		this.ship_Json = ship_Json;
	}
	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}
	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}
	public List<DlyType> getShipTypeList() {
		return shipTypeList;
	}
	public void setShipTypeList(List<DlyType> shipTypeList) {
		this.shipTypeList = shipTypeList;
	}
	public List<PayCfg> getPayTypeList() {
		return payTypeList;
	}
	public void setPayTypeList(List<PayCfg> payTypeList) {
		this.payTypeList = payTypeList;
	}
	public Map getOrderMap() {
		return orderMap;
	}
	public void setOrderMap(Map orderMap) {
		this.orderMap = orderMap;
	}
	public Integer getStype() {
		return stype;
	}
	public void setStype(Integer stype) {
		this.stype = stype;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getPaystatus() {
		return paystatus;
	}
	public void setPaystatus(Integer paystatus) {
		this.paystatus = paystatus;
	}
	public Integer getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
	}

 
}
