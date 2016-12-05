package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DlyCenter;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyCenterManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单管理action
 * 
 * @author kingapex 2010-4-7下午01:51:48;modify by dable
 * @author LiFenLong 2014-4-1;4.0版本改造
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("order")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/order/order_list.html"),
	@Result(name="trash_list", type="freemarker", location="/shop/admin/order/trash_list.html"),
	@Result(name="detail", type="freemarker", location="/shop/admin/order/order_detail.html"),
	@Result(name="detailBySN", type="freemarker", location="/shop/admin/order/order_detail.html"),
	@Result(name="not_ship", type="freemarker", location="/shop/admin/order/not_ship.html"),
	@Result(name="not_pay", type="freemarker", location="/shop/admin/order/not_pay.html"),
	@Result(name="not_rog", type="freemarker", location="/shop/admin/order/not_rog.html"),
	@Result(name="list_express", type="freemarker", location="/shop/admin/order/listForExpressNo.html")
})
@SuppressWarnings({"rawtypes","serial","static-access","unchecked"})
public class OrderAction extends WWAction {

	private String sn;
	private String logi_no;
	private String uname;
	private String start_time;
	private String end_time;
	private String ship_name;
	private Integer status=null;
	private Integer paystatus=null;
	private Integer shipstatus=null;
	private Integer shipping_type=null;
	private Integer payment_id=null;
	private int shipping_id;
	private Integer stype;
	private String keyword;
	
	private Integer orderId;
	private String searchKey;
	private String searchValue;

	
	//详细页面插件返回的数据 
	protected Map<Integer,String> pluginTabs;
	protected Map<Integer,String> pluginHtmls;

	private Order ord;
	private List provinceList;
	private String alert_null;
	private Integer[] order_id;
	private double price; //修改订单价格所用
	private double shipmoney; //修改运费所用
	private String remark;
	//退货单ID数组
	private String start;
	private String end;
	private String next;
	private List<Delivery> deliveryList;
	private String addr;
	private String ship_day;
	private String ship_tel;
	private String ship_mobile;
	private String ship_zip;
	private Integer member_id;
	
	private Map params;
	private List<DlyType> shipTypeList;
	private List<PayCfg> payTypeList;
	
	private List orderList;
	private Map orderMap;
	private Map statusMap;
	private Map payStatusMap;
	private Map shipMap;
	private String status_Json;
	private String payStatus_Json;
	private String ship_Json;
	private String orderstate;
	
	
	private Integer depotid; //仓库Id 
	private Integer paytypeid; //付款方式Id
	private String cancel_reason;//订单取消原因 2014-6-10 李奋龙
	private String complete;
	
	private List logi_list;
	private IDepotManager depotManager;
	private IOrderManager orderManager;
	private IRegionsManager regionsManager;
	private IOrderFlowManager orderFlowManager;
	private IPromotionManager promotionManager;
	private OrderPluginBundle orderPluginBundle;
	private IAdminUserManager adminUserManager;
	private IDlyTypeManager dlyTypeManager;
	private IPaymentManager paymentManager;
	private ILogiManager logiManager;
	//选择发货地点
		private IDlyCenterManager dlyCenterManager;
		private List<DlyCenter> dlycenterlist;
	/**
	 * 修改订单价格
	 * @param orderId 订单Id,Integer
	 * @param adminUser 管理员,AdminUser
	 * @param amount 订单总额,double
	 * @param price 修改后订单总额,double
	 * @return json
	 * result 1,操作成功.2,操作失败
	 */
	public String savePrice(){
		try{
			double amount = orderManager.get(this.orderId).getOrder_amount();
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			this.orderManager.savePrice(price, this.orderId);
			//记录日志
			orderManager.log(this.orderId, "订单价格从:"+amount+"修改为" + price, null, adminUser.getUsername());
			this.showSuccessJson("成功"+price);
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改订单配送费用
	 * @param orderId 订单Id,Integer
	 * @param currshipamount 修改前订单配送费用,double
	 * @param shipmoney 修改后订单配送费用,double
	 * @param price 修改后订单总价,double
	 * @param adminUser 管理员,AdminUser
	 * @return  json
	 * result 1,操作成功.2,操作失败
	 */
	public String saveShipMoney(){
		try{
			double currshipamount=orderManager.get(this.orderId).getShipping_amount();
			double price = this.orderManager.saveShipmoney(shipmoney, this.orderId);
			//记录日志
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			orderManager.log(this.orderId, "运费从"+currshipamount+"修改为" + price, null, adminUser.getUsername());
			this.json="{\"result\":1,\"price\":\""+price+"\"}";
			//this.showSuccessJson("保存成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("保存失败");
		}
		return this.JSON_MESSAGE;
		
	}
	/**
	 * 修改订单配送地区
	 * @param orderId 订单Id,Integer
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param Attr 配送地址,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 区Id,Integer
	 * @return  json
	 * result 1,操作成功.2,操作失败
	 */
	public String saveAddr(){
		try {
			//获取地区
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			String Attr=province+"-"+city+"-"+region;
			//获取地区Id
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			this.orderManager.saveAddr(this.orderId, StringUtil.toInt(province_id,true),StringUtil.toInt(city_id,true) ,StringUtil.toInt(region_id, true),Attr);
			this.showSuccessJson("保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("保存失败");
		}
		return this.JSON_MESSAGE;
	
	}
	/**
	 * 修改订单详细配送地址
	 * @param adminUser 管理员,AdminUser
	 * @param oldAddr 修改前订单详细配送地址,String
	 * @param addr 修改后订单详细配送地址,String
	 * @param orderId 订单Id,Integer
	 * @return json
	 * result 1,操作成功.2,操作失败
	 */
	public String saveAddrDetail(){
		try{
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			String oldAddr = this.orderManager.get(this.orderId).getShip_addr();
			boolean addr = this.orderManager.saveAddrDetail(this.addr, orderId);
			//记录日志
			this.orderManager.log(this.orderId, "收货人详细地址从['"+oldAddr+"']修改为['" + addr+"']", null, adminUser.getUsername());
			if(addr){
				this.showSuccessJson("成功");
			}else{
				this.showErrorJson("失败");
			}
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 修改 订单配送信息
	 * @param adminUser 管理员,AdminUser
	 * @param oldShip_day 修改前的收货日期,String
	 * @param oldship_name 修改前的收货人,String
	 * @param oldship_tel 修改前的收货人电话,String
	 * @param oldship_mobile 修改前的收货人手机号,String
	 * @param oldship_zip 修改前的邮编,String
	 * @param remark 修改后的订单备注,String
	 * @param ship_day 修改后的订单收货日期,String
	 * @param ship_name 修改后的收货人名称,String
	 * @param ship_tel 修改后的收货人电话,String
	 * @param ship_mobile 修改后的收货人手机号,String
	 * @param ship_zip 修改后的邮编 ,String
	 * @param orderId 订单Id,Integer
	 * @return
	 */
	public String saveShipInfo(){
		try{
			Order order = this.orderManager.get(this.orderId);
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			String oldShip_day = order.getShip_day();
			String oldship_name = order.getShip_name();
			String oldship_tel = order.getShip_tel();
			String oldship_mobile = order.getShip_mobile();
			String oldship_zip = order.getShip_zip();
			
			boolean addr = this.orderManager.saveShipInfo(remark,ship_day, ship_name, ship_tel, ship_mobile, ship_zip, this.orderId);
			//记录日志
			if(ship_day !=null && !StringUtil.isEmpty(ship_day)){
				this.orderManager.log(this.orderId, "收货日期从['"+oldShip_day+"']修改为['" + ship_day+"']", null, adminUser.getUsername());
			}if(ship_name !=null && !StringUtil.isEmpty(ship_name)){
				this.orderManager.log(this.orderId, "收货人姓名从['"+oldship_name+"']修改为['" + ship_name+"']", null, adminUser.getUsername());
			}if(ship_tel !=null && !StringUtil.isEmpty(ship_tel)){
				this.orderManager.log(this.orderId, "收货人电话从['"+oldship_tel+"']修改为['" + ship_tel+"']", null, adminUser.getUsername());
			}if(ship_mobile !=null && !StringUtil.isEmpty(ship_mobile)){
				this.orderManager.log(this.orderId, "收货人手机从['"+oldship_mobile+"']修改为['" + ship_mobile+"']", null, adminUser.getUsername());
			}if(ship_zip !=null && !StringUtil.isEmpty(ship_zip)){
				this.orderManager.log(this.orderId, "收货人邮编从['"+oldship_zip+"']修改为['" + ship_zip+"']", null, adminUser.getUsername());
			}
			if(addr){
				this.showSuccessJson("成功!");
			}else{
				this.showErrorJson("失败!");
			}
		}catch(RuntimeException e){
			this.showErrorJson("失败!"+e.getMessage());
		}
		return this.JSON_MESSAGE;
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
	public String list() {
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
		return "list";
	}
	/**
	 * 未发货订单
	 * @author LiFenLong;2014-4-18
	 * @param statusMap 订单状态集合,Map
	 * @param payStatusMap 订单付款状态集合,Map
	 * @param shipMap,订单配送人状态集合,Map
	 * @param shipTypeList 配送方式集合,List
	 * @param payTypeList 付款方式集合,List
	 * @return 未发货订单列表
	 */
	public String notShipOrder(){
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
		return "not_ship";
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
		return "not_pay";
	}
	/**
	 * 未收货订单
	 * @author LiFenLong
	 * @param statusMap 订单状态集合,Map
	 * @param payStatusMap 订单付款状态集合,Map
	 * @param shipMap,订单配送人状态集合,Map
	 * @param shipTypeList 配送方式集合,List
	 * @param payTypeList 付款方式集合,List
	 * @return 未收货订单列表
	 */
	public String notRogOrder(){
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
		return "not_rog";
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
		orderMap.put("sn", sn);
		orderMap.put("ship_name", ship_name);
		orderMap.put("status", status);
		orderMap.put("paystatus", paystatus);
		orderMap.put("shipstatus", shipstatus);
		orderMap.put("shipping_type", shipping_type);
		orderMap.put("payment_id", payment_id);
		orderMap.put("order_state", requst.getParameter("order_state"));
		orderMap.put("complete", complete);
		
		this.webpage = this.orderManager.listOrder(orderMap, this.getPage(),this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		return JSON_MESSAGE;

	}
	/**
	 * 回收站订单
	 * @author LiFenLong
	 * @param statusMap 订单状态集合,Map
	 * @param payStatusMap 订单付款状态集合,Map
	 * @param shipMap,订单配送人状态集合,Map
	 * @param shipTypeList 配送方式集合,List
	 * @param payTypeList 付款方式集合,List
	 * @return 回收站订单列表
	 */
	public String trash_list() {
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
		
		return "trash_list";
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
	 * @param sort 排序,Integer
	 * @return 回收站订单列表 json
	 */
	public String trash_listJson(){
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
		
		this.webpage = this.orderManager.list(this.getPage(), this.getPageSize(), 1, this.getSort());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 将订单添加至回收站
	 * @param order_id 订单Id数组,Integer[]
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String delete() {
		try {
			if(this.orderManager.delete(order_id)){
				this.showSuccessJson("订单加入回收站成功");
			}else{
				this.showErrorJson("您所删除的订单包含未作废的订单，无法加入回收站");
			}
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("订单删除失败"+e.getMessage());

		}
		return this.JSON_MESSAGE;

	}
	/**
	 * 将订单还原
	 * @param order_id 订单号数组,Integer[]
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String revert() {
		try {
			this.orderManager.revert(order_id);
			this.showSuccessJson("订单还原成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("订单还原失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;

	}
	/**
	 * 将订单清除
	 * @param order_id 订单号数组,Integer[]
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String clean() {
		try {
			this.orderManager.clean(order_id);
			this.showSuccessJson("订单清除成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("订单清除失败"+e.getMessage());

		}
		return this.JSON_MESSAGE;

	}

	

	/**
	 * 完成订单
	 * @param orderId 订单号,Integer
	 * @param order 订单,Order
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String complete() {
		try {
			this.orderFlowManager.complete(orderId);
			Order order = this.orderManager.get(orderId);
			this.showSuccessJson("订单[" + order.getSn()+ "]成功标记为完成状态");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("订单完成失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 作废订单
	 * @param orderId 订单号,Integer
	 * @param cancel_reason 作废原因,String
	 * @param order 订单,Order
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String cancel() {
		try {
			this.orderFlowManager.cancel(orderId,cancel_reason);
			Order order = this.orderManager.get(orderId);
			this.json = "{result:1,message:'订单[" + order.getSn()+ "]成功作废',orderStatus:" + order.getStatus() + "}";
			this.showSuccessJson("订单作废成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			//this.json = "{result:0,message:\"订单作废失败：" + e.getMessage() + "\"}";
			this.showErrorJson("订单作废失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 确定收货
	 * @author LiFenLong
	 * @param adminUser 管理员,AdminUser
	 * @param orderId 订单Id,Integer
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String rogConfirm() {
		try {
			AdminUser adminUser  = UserConext.getCurrentAdminUser();
			this.orderFlowManager.rogConfirm(this.orderId, adminUser.getUserid(), adminUser.getUsername(), adminUser.getUsername(),DateUtil.getDateline());
			this.showSuccessJson("确认收货成功");
		} catch (Exception e) {
			this.showErrorJson("数据库错误");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 *  确认订单
	 * @param orderId 订单号,Integer
	 * @param order 订单,Order
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String confirmOrder(){
		try{
			this.orderFlowManager.confirmOrder(orderId);
			Order order = this.orderManager.get(orderId);
			this.orderFlowManager.addCodPaymentLog(order);
			this.showSuccessJson("'订单[" + order.getSn()+"]成功确认'");
		}catch(RuntimeException e){
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("订单确认失败"+e.getMessage());
		}
		return this.JSON_MESSAGE;
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
	public String detail(){
		
		if(ship_name!=null ) ship_name = StringUtil.toUTF8(ship_name);
		if(uname!=null ) uname = StringUtil.toUTF8(uname);
		this.ord = this.orderManager.get(orderId);
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
		if(ship_name!=null ) ship_name = StringUtil.toUTF8(ship_name);
		if(uname!=null ) uname = StringUtil.toUTF8(uname);
		this.ord = this.orderManager.get(sn);
		orderId = ord.getOrder_id();
		/*logi_no = ord.getLogi_name();
		uname = ""*/
		provinceList = this.regionsManager.listProvince();
		
		pluginTabs= this.orderPluginBundle.getTabList(ord);
		pluginHtmls = this.orderPluginBundle.getDetailHtml(ord);
		dlycenterlist= dlyCenterManager.list();
		
		return "detail";
	}
	 
	
	/**
	 * 下一订单，或者上一订单
	 * @param orderId 订单号,Integer
	 * @param status 订单状态,Integer
	 * @param sn 订单编号,Integer
	 * @param uname 会员名称,String
	 * @param ship_name 收货人姓名,String
	 * @param listProvince 省列表,List
	 * @param pluginTabs 订单详细页的选项卡
	 * @param pluginHtmls 订单详细页的内容
	 * @return 订单详细页面
	 */
	public String nextDetail() {
		if(this.orderManager.getNext(next, orderId, status, 0, sn, logi_no, uname, ship_name)==null){
			this.alert_null = "kong";
			this.ord=this.orderManager.get(orderId);
		}else{
			this.ord =this.orderManager.getNext(next, orderId, status, 0, sn, logi_no, uname, ship_name);
		}
		 
		this.orderId = ord==null?this.orderId:ord.getOrder_id();
		provinceList = this.regionsManager.listProvince();
		

		pluginTabs= this.orderPluginBundle.getTabList(ord);
		pluginHtmls = this.orderPluginBundle.getDetailHtml(ord);
		
		
		return "detail";
	}

	/**
	 * 添加管理员备注
	 * @param remark 备注内容
	 * @param orderId 订单号,Integer
	 * @param ord 订单,Order
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String saveAdminRemark(){
	 
		this.ord = this.orderManager.get(orderId);
		this.ord.setAdmin_remark(remark);
		try{
			this.orderManager.edit(this.ord);
			this.showSuccessJson("修改成功");
		}catch(RuntimeException e){
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("修改失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 修改会员———查询会员订单
	 * @author xulipeng
	 * @param member_id 会员Id,Integer
	 * @param list 会员订单
	 * @return 会员订单json
	 */
	public String listOrderByMemberId(){
	    Page list = this.orderManager.listByStatus(this.getPage(), this.getPageSize(), member_id);
		this.showGridJson(list);
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取订单状态
	 * @return 订单状态集合
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		
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
	
		//暂停使用的订单状态
//		orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
//		orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM)); 
//		orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
		return orderStatus;
	}
	
	/**
	 * 获取订单付款状态
	 * @return 订单付款状态集合
	 */
	private Map getpPayStatusJson(){
		
		Map pmap = new HashMap();
		pmap.put(""+OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
	//	pmap.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
		pmap.put(""+OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
		pmap.put(""+OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
		pmap.put(""+OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));

		return pmap;
	}
	/**
	 * 获取订单配送状态
	 * @return 订单配送状态集合
	 */
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
	/**
	 * 保存库房
	 * @author LiFenLong
	 * @param orderId 订单Id,Integer
	 * @param depotid 库房id,Integer
	 * @param oldname 修改前库房名称
	 * @param depotname 修改后库房名称
	 * @param adminUser 管理员,AdminUser
	 * @return  json
	 * result 1。操作成功.0.操作失败
	 */
	public String saveDepot(){
		
		try {
			String oldname = this.depotManager.get(this.orderManager.get(orderId).getDepotid()).getName();
			String depotname = this.depotManager.get(depotid).getName();
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			this.orderManager.saveDepot(orderId, depotid);
			//记录日志
			this.orderManager.log(orderId, "修改仓库从"+oldname+"修改为" + depotname, adminUser.getUserid(), adminUser.getUsername());
			this.showSuccessJson("保存库房成功");
		} catch (Exception e) {
			this.showErrorJson("保存库房出错:"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 保存付款方式
	 * @author LiFenLong
	 * @param orderId 订单Id,Integer
	 * @param paytypeid 付款方式Id,Integer
	 * @return  json
	 * result 1。操作成功.0.操作失败
	 */
	public String savePayType(){	
		try {
			this.orderManager.savePayType(this.orderId, this.paytypeid);
			this.showSuccessJson("保存配送方式成功");
		} catch (Exception e) {
			this.logger.error("保存配送方式出错",e);
			this.showErrorJson("保存配送方式出错");
		}
		
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 保存配送方式 
	 * @param orderId 订单Id,Integer
	 * @param shipping_id 配送方式Id,Integer
	 * @return json
	 * result 1。操作成功.0.操作失败
	 */
	public String saveShipType(){
		
		try {
			this.orderManager.saveShipType(this.orderId, this.shipping_id);
			this.showSuccessJson("保存配送方式成功");
		} catch (Exception e) {
			this.logger.error("保存配送方式出错",e);
			this.showErrorJson("保存配送方式出错");
		}
		
		
		return this.JSON_MESSAGE;
	}
	/**
	 * 填写快递单号
	 * @return
	 */
	public String listForExpressNo(){
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		String ordersId= requst.getParameter("orderIds");
		String[] order_idstr=ordersId.split(",");
		int count=1;
		
		//如果字符串中有c
		while( ordersId.indexOf(",")!=-1){
		  count++;
		  ordersId = ordersId.substring(ordersId.indexOf(",")+1);
		} 
		Integer[] order_idstrint=new Integer[count];
		for (int i = 0; i < order_idstr.length; i++) {
			order_idstrint[i] = NumberUtils.toInt(order_idstr[i]);
		}
		orderList = this.orderManager.listByOrderIds(order_idstrint,null);
		if(!orderList.isEmpty()){
			boolean isSame = true;
			Order first = (Order)orderList.get(0);
			int firstShip =first.getShipping_id();
			for (Object o : orderList) {
				Order ord = (Order)o;
				if(ord.getShipping_id() !=firstShip){
					isSame=false;
					break;
				}
			}
			params = new HashMap();
			if(isSame){
				params.put("ship_type", ""+firstShip);
			}else{
				params.put("ship_type", "not_same");
			}
		}
		logi_list = logiManager.list();
		return "list_express";
	}
	//setter getter
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getLogi_no() {
		return logi_no;
	}
	public void setLogi_no(String logi_no) {
		this.logi_no = logi_no;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
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
	public String getShip_name() {
		return ship_name;
	}
	public void setShip_name(String ship_name) {
		this.ship_name = ship_name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public int getShipping_id() {
		return shipping_id;
	}
	public void setShipping_id(int shipping_id) {
		this.shipping_id = shipping_id;
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
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}
	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}
	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}
	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}
	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
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
	public Order getOrd() {
		return ord;
	}
	public void setOrd(Order ord) {
		this.ord = ord;
	}
	public List getProvinceList() {
		return provinceList;
	}
	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}
	public String getAlert_null() {
		return alert_null;
	}
	public void setAlert_null(String alert_null) {
		this.alert_null = alert_null;
	}
	public Integer[] getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer[] order_id) {
		this.order_id = order_id;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getShipmoney() {
		return shipmoney;
	}
	public void setShipmoney(double shipmoney) {
		this.shipmoney = shipmoney;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public List<Delivery> getDeliveryList() {
		return deliveryList;
	}
	public void setDeliveryList(List<Delivery> deliveryList) {
		this.deliveryList = deliveryList;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getShip_day() {
		return ship_day;
	}
	public void setShip_day(String ship_day) {
		this.ship_day = ship_day;
	}
	public String getShip_tel() {
		return ship_tel;
	}
	public void setShip_tel(String ship_tel) {
		this.ship_tel = ship_tel;
	}
	public String getShip_mobile() {
		return ship_mobile;
	}
	public void setShip_mobile(String ship_mobile) {
		this.ship_mobile = ship_mobile;
	}
	public String getShip_zip() {
		return ship_zip;
	}
	public void setShip_zip(String ship_zip) {
		this.ship_zip = ship_zip;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
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
	public Map getParams() {
		return params;
	}
	public void setParams(Map params) {
		this.params = params;
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
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public List getOrderList() {
		return orderList;
	}
	public void setOrderList(List orderList) {
		this.orderList = orderList;
	}
	public Map getOrderMap() {
		return orderMap;
	}
	public void setOrderMap(Map orderMap) {
		this.orderMap = orderMap;
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
	public String getOrderstate() {
		return orderstate;
	}
	public void setOrderstate(String orderstate) {
		this.orderstate = orderstate;
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
	public Integer getDepotid() {
		return depotid;
	}
	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}
	public Integer getPaytypeid() {
		return paytypeid;
	}
	public void setPaytypeid(Integer paytypeid) {
		this.paytypeid = paytypeid;
	}
	public String getCancel_reason() {
		return cancel_reason;
	}
	public void setCancel_reason(String cancel_reason) {
		this.cancel_reason = cancel_reason;
	}
	public String getComplete() {
		return complete;
	}
	public void setComplete(String complete) {
		this.complete = complete;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	public List getLogi_list() {
		return logi_list;
	}
	public void setLogi_list(List logi_list) {
		this.logi_list = logi_list;
	}
	public ILogiManager getLogiManager() {
		return logiManager;
	}
	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}
}
