package com.enation.app.b2b2c.core.service.order;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.framework.database.Page;

/**
 * 店铺订单管理类
 * @author LiFenLong
 *@version v2.0 modify by kingapex-2015-08-21
 *v2.0 修改了以下内容：
 *增加创建订单的接口，因为要将单店和多店创建订单分开
 */
public interface IStoreOrderManager {
	

	/**
	 * 创建订单<br>
	 * 在这里首先要通过order核心api来创建主订单，然后创建子订单。<br>
	 * 和单店系统另外的区别是子订单价格计算事件调用另外的事件
	 * @param order 要创建的订单
	 *            订单实体:<br/>
	 *            <li>shipping_id(配送方式id):需要填充用户选择的配送方式id</li> <li>
	 *            regionid(收货地区id)</li> <li>是否保价is_protect</li>
	 *            shipping_area(配送地区):需要填充以下格式数据：北京-北京市-昌平区 </li>
	 * 
	 *            <li>
	 *            payment_id(支付方式id):需要填充为用户选择的支付方式</li>
	 * 
	 *            <li>填充以下收货信息：</br> ship_name(收货人姓名)</br> ship_addr(收货地址)</br>
	 *            ship_zip(收货人邮编)</br> ship_email(收货人邮箱 ) ship_mobile( 收货人手机)
	 *            ship_tel (收货人电话 ) ship_day (送货日期 ) ship_time ( 送货时间 )
	 * 
	 *            </li> <li>remark为买家附言</li>
	 *            
	 * @param shippingIds 
	 *   		配送方式id数据，根据购物车中店铺顺序形成           
	 * @param sessionid
	 *  	  会员的sessionid
	 * @return
	 *        创建的新订单实体，已经赋予order id
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Order createOrder(Order order,String sessionid);
	/**
	 *lxl   
	 * @param order
	 * @param sessionid
	 * @param cart_id
	 * @return
	 */
    public Order createOrderForApp(Order order, String sessionid, String cart_id);

	
	/**
	 * 查看店铺订单
	 * @param pageNo
	 * @param pageSize
	 * @param storeid
	 * @param map
	 * @return 店铺订单列表
	 */
	public Page storeOrderList(Integer pageNo,Integer pageSize,Integer storeid,Map map);
	/**
	 * 查看店铺子订单
	 * @param parent_id
	 * @return 店铺子订单列表
	 */
	public List<StoreOrder> storeOrderList(Integer parent_id);
	/**
	 * 获取一个订单
	 * @param orderId
	 * @return StoreOrder
	 */
	public StoreOrder get(Integer orderId);
	/**
	 * 修改收货人信息
	 * @param remark
	 * @param ship_day
	 * @param ship_name
	 * @param ship_tel
	 * @param ship_mobile
	 * @param ship_zip
	 * @param orderid
	 * @return boolean
	 */
	public boolean saveShipInfo(String remark,String ship_day, String ship_name,String ship_tel, String ship_mobile, String ship_zip, int orderid);
	/**
	 * 查看店铺会员订单
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param keyword
	 * @return Page
	 */
	public Page pageOrders(int pageNo, int pageSize,String status, String keyword);
	
	
	/**
	 * 根据订单编号查看订单
	 * @param ordersn
	 * @return StoreOrder
	 */
	public StoreOrder get(String ordersn);
	/**
	 * 根据订单状态获取店铺订单数量
	 * @param struts
	 * @author LiFenLong
	 * @return
	 */
	public int getStoreOrderNum(int struts);
	
	/**
	 * 查询订单列表
	 * @author LiFenLong 
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param other
	 * @param sort
	 * @return
	 */
	public Page listOrder(Map map,int page,int pageSize,String sort,String order);
	
	/**
	 * 获取订单状态的json
	 * @return 订单状态Json
	 */
	public Map getStatusJson();
	/**
	 * 获取付款状态的json
	 * @return 付款状态Json
	 */
	public Map getpPayStatusJson();
	/**
	 * 获取配送状态的json
	 * @return 配送状态Json
	 */
	public Map getShipJson();
	/**
	 * 发货
	 * @param order_id 订单ID
	 * @param logi_id 物流公司id
	 * @param logi_name 物流公司名称
	 * @param shipNo 运单号
	 */
	public void saveShipNo(Integer[] order_id,Integer logi_id,String logi_name,String shipNo);
	
	/**
	 * 获得该会员订单在各个状态的个数
	 * 
	 */
	public Integer orderStatusNum(Integer status);
	
	/**
	 * 通过商铺ID，获得该商铺下的商品个数
	 * @param store_id
	 * @return
	 */
	public Integer getStoreGoodsNum(int store_id);


	/**
	 * 获取会员所有子订单
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param keyword
	 * @return
	 */
	public Page pageChildOrders(int pageNo, int pageSize, String status, String keyword);
	
	
	/**
	 * 统计订单状态
	 * @return key为状态值 ，value为此状态订单的数量
	 */
	public Map  censusState();

	/**
	 * 获得订单列表
	 */
	public Map<Order, List<StoreOrder>> listForErp(int max);

	/**
	 * 获取product的重量map
	 * @param productIds
	 * @return
     */
	public Map<Integer, Double> getProductWeightMap(Integer... productIds);

	public List<OrderItem> getOmsRefundOrderItems(Order so);
}
