package com.enation.app.b2b2c.core.action.backend.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.store.Store;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.DlyCenter;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IDlyCenterManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

@Component
/**
 * 店铺订单管理
 * @author LiFenLong
 *
 */
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/order/store_order_list.html"),
	 @Result(name="not_pay",type="freemarker", location="/b2b2c/admin/order/not_pay.html"),
	 @Result(name="detail",type="freemarker", location="/b2b2c/admin/order/order_detail.html"),
	 @Result(name="detailBySN", type="freemarker", location="/b2b2c/admin/order/order_detail_show.html")

})
@Action("storeOrder")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class StoreOrderAction extends WWAction{
	private StoreOrder ord;
	
	private Integer orderId;
	private Integer stype;
	private Integer status=null;
	private Integer paystatus=null;
	private Integer shipstatus=null;
	private Integer shipping_type=null;
	private Integer payment_id=null;
	
	private String uname;
	private String keyword;
	private String start_time;
	private String end_time;
	private String sn;
	private String logi_no;
	private String ship_name;
	private String status_Json;
	private String payStatus_Json;
	private String ship_Json;
	private String complete;
	private String store_name;
	private Integer store_id;
	private Store store;
	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
	private Map orderMap;
	private Map statusMap;
	private Map payStatusMap;
	private Map shipMap;
	//详细页面插件返回的数据 
	protected Map<Integer,String> pluginTabs;
	protected Map<Integer,String> pluginHtmls;
	
	private List provinceList;
	private List<DlyType> shipTypeList;
	private List<PayCfg> payTypeList;
	private List<Map> orderChildList;
	
	private IDlyTypeManager dlyTypeManager;
	private IPaymentManager paymentManager;
	private IRegionsManager regionsManager;
	private IOrderManager orderManager;
	private IStoreOrderManager storeOrderManager;
	private OrderPluginBundle orderPluginBundle;
	
	private IStoreManager storeManager;
	

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	//选择发货地点
	private IDlyCenterManager dlyCenterManager;
	private List<DlyCenter> dlycenterlist;
	
	/**
	  * 跳转至订单详细页面
	  * @param uname 会员名称,String
	  * @param ship_name 收货人姓名,String
	  * @param orderId 订单号,Integer
	  * @param ord 订单,Order
	  * @param provinceList 省列表
	  * @param pluginTabs 订单详细页的选项卡
	  * @param pluginHtmls 订单详细页的内容
	  * @param dlycenterlist 发货信息列表
	  * @return 订单详细页面
	  */
	public String orderDetail(){
		if(ship_name!=null ) ship_name = StringUtil.toUTF8(ship_name);
		if(uname!=null ) uname = StringUtil.toUTF8(uname);


		this.ord = storeOrderManager.get(orderId);
		
		store = storeManager.getStore(ord.getStore_id());
		ord.setStore_type_name(store.getTel());
		if (this.ord != null && this.ord.getParent_id() != null) {
			Order parentOrder = this.orderManager.get(this.ord.getParent_id());
			this.ord.setParentOrder(parentOrder);
		}

		provinceList = this.regionsManager.listProvince();
		pluginTabs= this.orderPluginBundle.getTabList(ord);
		pluginHtmls = this.orderPluginBundle.getDetailHtml(ord);
		dlycenterlist= dlyCenterManager.list();
		return "detail";
	}

    /**
     * 跳转至订单详细页面
     * @param uname 会员名称,String
     * @param ship_name 收货人姓名,String
     * @param orderId 订单号,Integer
     * @param ord 订单,Order
     * @param provinceList 省列表
     * @param pluginTabs 订单详细页的选项卡
     * @param pluginHtmls 订单详细页的内容
     * @param dlycenterlist 发货信息列表
     * @return 订单详细页面
     */
   public String detailBySN(){
       this.ord = this.storeOrderManager.get(sn);
       orderId = ord.getOrder_id();
       provinceList = this.regionsManager.listProvince();
       
       pluginTabs= this.orderPluginBundle.getTabList(ord);
       pluginHtmls = this.orderPluginBundle.getDetailHtml(ord);
       dlycenterlist= dlyCenterManager.list();
       
       return "detailBySN";
   }
    
	/**
	 * 分页读取订单列表
	 * 根据订单状态 state 检索，如果未提供状态参数，则检索所有
	 * @param statusMap 订单状态集合,Map
	 * @param payStatusMap 订单付款状态集合,Map
	 * @param shipMap,订单配送人状态集合,Map
	 * @param shipTypeList 配送方式集合,List
	 * @param payTypeList 付款方式集合,List
	 * @return 订单列表
	 */
	public String list(){
		if(statusMap==null){
			statusMap = getStatusJson();
			String p= JSONArray.fromObject(statusMap).toString();
			status_Json=p.replace("[", "").replace("]", "");
		}
		
		if(payStatusMap==null){
			payStatusMap = getpPayStatusJson();
			String p= JSONArray.fromObject(payStatusMap).toString();
			payStatus_Json=p.replace("[", "").replace("]", "");
			
		}
		
		if(shipMap ==null){
			shipMap = getShipJson();
			String p= JSONArray.fromObject(shipMap).toString();
			ship_Json = p.replace("[", "").replace("]", "");
			
		}
		shipTypeList = dlyTypeManager.list();
		payTypeList = paymentManager.list();
		return "list";
	}
	/**
	 * @author LiFenLong
	 * @param stype 搜索状态, Integer
	 * @param keyword 搜索关键字,String
	 * @param start_time 开始时间,String
	 * @param end_time 结束时间,String
	 * @param sn 订单编号,String
	 * @param ship_name 订单收货人姓名,String
	 * @param status 订单状态,Integer
	 * @param paystatus 订单付款状态,Integer
	 * @param shipstatus 订单配送状态,Integer
	 * @param shipping_type 配送方式,Integer
	 * @param payment_id 付款方式,Integer
	 * @param order_state 订单状态_从哪个页面进来搜索的(未付款、代发货、等),String
	 * @param complete 是否订单为已完成,String 
	 * @return 订单列表 json
	 */
	
	public String listJson(){
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		orderMap = new HashMap();
		orderMap.put("stype", stype);
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("status", status);
		orderMap.put("sn", sn);
		orderMap.put("ship_name", ship_name);
		orderMap.put("paystatus", paystatus);
		orderMap.put("shipstatus", shipstatus);
		orderMap.put("shipping_type", shipping_type);
		orderMap.put("payment_id", payment_id);
		orderMap.put("order_state", requst.getParameter("order_state"));
		orderMap.put("complete", complete);
		orderMap.put("store_name", store_name);
		orderMap.put("store_id", store_id);

		this.webpage = this.storeOrderManager.listOrder(orderMap, this.getPage(),this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	/**
	 * 获取订单状态的json
	 * @return
	 */
	private Map<String, String> getStatusJson(){
		Map<String, String> orderStatus = new  HashMap<>();
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));// 未付款/新订单       修改为已确认
		orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));//订单已生效
		orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));// 已确认支付
		orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));//配货完成，待发货
		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));// 已发货
		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));// 已收货
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));// 退货
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));// 已完成
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));// 退款
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));// 作废
		orderStatus.put(""+OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));//已换货
		orderStatus.put(""+OrderStatus.ORDER_CHANGE_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));//申请换货
		orderStatus.put(""+OrderStatus.ORDER_RETURN_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));// 申请退货
		orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));// 已支付待确认
		orderStatus.put(""+OrderStatus.ORDER_RETURNED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURNED));//已退货
		orderStatus.put(""+OrderStatus.ORDER_CHANGE_REFUSE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_REFUSE));//换货被拒绝
        orderStatus.put(""+OrderStatus.ORDER_RETURN_REFUSE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_REFUSE));//退货被拒绝
        orderStatus.put(""+OrderStatus.ORDER_ALLOCATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION));//配货中
		return orderStatus;
	}
	
	
	private Map<String, String> getpPayStatusJson(){
		Map<String, String> pmap = new HashMap<>();
		pmap.put(""+OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
	//	pmap.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
		pmap.put(""+OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
		pmap.put(""+OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
		pmap.put(""+OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));
		return pmap;
	}
	
	private Map<String, String> getShipJson(){
		Map<String, String> map = new HashMap<>();
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
	/**
	 * 未付款订单
	 * @author LiFenLong
	 * @param statusMap 订单状态集合,Map
	 * @param payStatusMap 订单付款状态集合,Map
	 * @param shipMap,订单配送人状态集合,Map
	 * @param shipTypeList 配送方式集合,List
	 * @param payTypeList 付款方式集合,List
	 * @return 未付款订单列表
	 */
	public String notPayOrder(){
		if(statusMap==null){
			statusMap = getStatusJson();
			String p= JSONArray.fromObject(statusMap).toString();
			status_Json=p.replace("[", "").replace("]", "");
		}
		if(payStatusMap==null){
			payStatusMap = getpPayStatusJson();
			String p= JSONArray.fromObject(payStatusMap).toString();
			payStatus_Json=p.replace("[", "").replace("]", "");
		}
		if(shipMap ==null){
			shipMap = getShipJson();
			String p= JSONArray.fromObject(shipMap).toString();
			ship_Json = p.replace("[", "").replace("]", "");
		}
		shipTypeList = dlyTypeManager.list();
		payTypeList = paymentManager.list();
		return "not_pay";
	}

	public StoreOrder getOrd() {
		return ord;
	}

	public void setOrd(StoreOrder ord) {
		this.ord = ord;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public List<Map> getOrderChildList() {
		return orderChildList;
	}
	public void setOrderChildList(List<Map> orderChildList) {
		this.orderChildList = orderChildList;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getShip_name() {
		return ship_name;
	}
	public void setShip_name(String ship_name) {
		this.ship_name = ship_name;
	}
	public Integer getPaystatus() {
		return paystatus;
	}
	public void setPaystatus(Integer paystatus) {
		this.paystatus = paystatus;
	}
	public Integer getShipstatus() {
		return shipstatus;
	}
	public void setShipstatus(Integer shipstatus) {
		this.shipstatus = shipstatus;
	}
	public Integer getShipping_type() {
		return shipping_type;
	}
	public void setShipping_type(Integer shipping_type) {
		this.shipping_type = shipping_type;
	}
	public Integer getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
	}
	public String getComplete() {
		return complete;
	}
	public void setComplete(String complete) {
		this.complete = complete;
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
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}
	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}
	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}
	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}
	public List getProvinceList() {
		return provinceList;
	}
	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}
	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}
	public IDlyCenterManager getDlyCenterManager() {
		return dlyCenterManager;
	}
	public void setDlyCenterManager(IDlyCenterManager dlyCenterManager) {
		this.dlyCenterManager = dlyCenterManager;
	}
	public List<DlyCenter> getDlycenterlist() {
		return dlycenterlist;
	}
	public void setDlycenterlist(List<DlyCenter> dlycenterlist) {
		this.dlycenterlist = dlycenterlist;
	}
	public String getLogi_no() {
		return logi_no;
	}
	public void setLogi_no(String logi_no) {
		this.logi_no = logi_no;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	
}
