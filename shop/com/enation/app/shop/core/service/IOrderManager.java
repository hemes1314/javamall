package com.enation.app.shop.core.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;

/**
 * 订单管理
 *
 * @author kingapex 2010-4-6上午11:09:53
 * @author LiFenLong 2014-4-22;4.0订单流程改版
 */
@SuppressWarnings("rawtypes")
public interface IOrderManager {
    /**
     * 记录订单操作日志
     *
     * @param order_id
     * @param message
     * @param op_id
     * @param op_name
     * @author LiFenLong
     */
    public void log(Integer order_id, String message, Long op_id, String op_name);

    /**
     * 修改订单价格
     *
     * @param price   订单总金额
     * @param orderid
     */
    public void savePrice(double price, int orderid);

    /**
     * 修改订单配送费用
     *
     * @param shipmoney
     * @param orderid
     * @return
     */
    public double saveShipmoney(double shipmoney, int orderid);


    public boolean saveAddrDetail(String addr, int orderid);


    public boolean saveShipInfo(String remark, String ship_day, String ship_name, String ship_tel, String ship_mobile, String ship_zip, int orderid);//修改收货人信息

    /**
     * 拒绝退货
     */
    public void refuseReturn(String orderSn);

    /**
     * 创建订单 计算如下业务逻辑：</br> <li>为订单创建唯一的sn(订单号)</li> <li>
     * 根据sessionid读取购物车计算订商品价格及商品重量，填充以下信息:</br> goods_amount
     * 商品总额,shipping_amount 配送费用,order_amount 订单总额,weight商品重量,商品数量：goods_num</li>
     * <li>根据shipping_id(配送方式id)、regionid(收货地区id)及is_protect(是否保价) 计算
     * protect_price</li> <li>根据payment_id(支付方式id)计算paymoney(支付费用)</li> <li>
     * 读取当前买家是否为会员或匿名购买并填充member_id字段</li> <li>计算获得积分和消费积分</li>
     *
     * @param order        订单实体:<br/>
     *                     <li>shipping_id(配送方式id):需要填充用户选择的配送方式id</li> <li>
     *                     regionid(收货地区id)</li> <li>是否保价is_protect</li>
     *                     shipping_area(配送地区):需要填充以下格式数据：北京-北京市-昌平区 </li>
     *                     <p>
     *                     <li>
     *                     payment_id(支付方式id):需要填充为用户选择的支付方式</li>
     *                     <p>
     *                     <li>填充以下收货信息：</br> ship_name(收货人姓名)</br> ship_addr(收货地址)</br>
     *                     ship_zip(收货人邮编)</br> ship_email(收货人邮箱 ) ship_mobile( 收货人手机)
     *                     ship_tel (收货人电话 ) ship_day (送货日期 ) ship_time ( 送货时间 )
     *                     <p>
     *                     </li> <li>remark为买家附言</li>
     * @param cartItemList 2015-08-20新增by kingapex
     *                     购物车列表<br>
     *                     以前是通过sessionid获取，现改为通过接口传递参数方式<br>
     *                     目的是解决比如在多店中需要创建子订单时需要传递某个店铺的购物列表，而不是全部的购物列表
     * @param sessionid    会员的sessionid
     * @return 创建的新订单实体，已经赋予order id
     * @throws IllegalStateException 会员尚未登录,不能兑换赠品!
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Order add(Order order, List<CartItem> cartItemList, String sessionid, OrderPrice mainOrderPrice);

    /**
     * 修改订单信息
     *
     * @param order
     */
    public void edit(Order order);

    /**
     * 分页读取订单列表
     *
     * @param page     页数
     * @param pageSize 每页显示数量
     * @param disabled 是否读回收站列表(1为读取回收站列表,0为读取正常订单列表)
     * @param order    排序值
     * @return 订单分页列表对象
     */
    public Page list(int page, int pageSize, int disabled, String order);

    /**
     * 分页读取订单列表
     *
     * @param page页数
     * @param pageSize 每页显示数量
     * @param status   订单状态
     * @param depotid  仓库标识
     * @param order    排序值
     * @return 订单分页列表对象
     */
    public Page list(int page, int pageSize, int status, int depotid, String order);

    /**
     * 查询确认付款订单
     *
     * @param pageNo
     * @param pageSize
     * @param order
     * @return
     */
    public Page listConfirmPay(int pageNo, int pageSize, String sort, String order);

    /**
     * 根据订单id获取订单详细
     *
     * @param orderId 订单id
     * @return 订单实体 对象
     * @throws ObjectNotFoundException 当订单不存在时
     */
    public Order get(Integer orderId);


    /**
     * 查询上一订单或者下一订单
     *
     * @param orderId
     * @return
     */
    public Order getNext(String next, Integer orderId, Integer status, int disabled, String sn,
                         String logi_no, String uname, String ship_name);

    /**
     * 根据订单号获取订单
     *
     * @param ordersn
     * @return
     */
    public Order get(String ordersn);


    /**
     * 读取某个订单的商品货物列表
     *
     * @param orderId 订单id
     * @return list中为map，对应order_items表
     */
    public List<OrderItem> listGoodsItems(Integer orderId);

    /**
     * 读取某个订单的商品货物列表(包含es_goods表信息)
     *
     * @param orderId 订单id
     * @return list中为map，对应order_items表
     */
	public List getOrderItem(int order_id);


    /**
     * 读取某订单的订单日志
     *
     * @param orderId
     * @return lisgt中为map ，对应order_log表
     */
    public List listLogs(Integer orderId);

    /**
     * 批量将某些订单放入回收站<br>
     *
     * @param orderId 要删除的订单Id数组
     */
    public boolean delete(Integer[] orderId);

    /**
     * 彻底删除某些订单 <br>
     * 同时删除以下信息： <li>订单购物项</li> <li>订单日志</li> <li>订单支付、退款数据</li> <li>订单发货、退货数据</li>
     *
     * @param orderId 要删除的订单Id数组
     */
    public void clean(Integer[] orderId);

    /**
     * 批量还原某些订单
     *
     * @param orderId
     */
    public void revert(Integer[] orderId);

    /**
     * 列表某会员的订单<br/>
     * lzf add
     *
     * @param member_id
     * @return
     */
    public List listOrderByMemberId(long member_id);

    /**
     * 取某一会员的关于订单的统计信息
     *
     * @param member_id
     * @return
     */
    public Map mapOrderByMemberId(long member_id);


    /**
     * 读取某订单的配件发货项
     *
     * @param orderid
     * @return
     */
    public List<Map> listAdjItem(Integer orderid);

    /**
     * 统计订单状态
     *
     * @return key为状态值 ，value为此状态订单的数量
     */
    public Map censusState();


    /**
     * 导出订单为excel
     *
     * @param start 下单日期范围开始
     * @param end   下单日期范围结束
     * @return 返回导出的excel下载路径
     */
    public String export(Date start, Date end);


    /**
     * 获取某个订单货物项
     *
     * @param itemid
     * @return 订单货物项
     */
    public OrderItem getItem(int itemid);


    /**
     * 取某一会员未付款的订单数
     *
     * @param member_id
     * @param status
     * @return
     */
    public int getMemberOrderNum(long member_id, int payStatus);

    /**
     * 根据订单ID查所有货物
     *
     * @param order_id订单ID
     * @return
     */
    public List<Map> getItemsByOrderid(Integer order_id);

    /**
     * 更新订单价格
     */
    public void updateOrderPrice(double price, int orderid);

    /**
     * 根据id查询物流公司名字
     */
    public String queryLogiNameById(Integer logi_id);

    /**
     * 游客订单查询
     *
     * @param page
     * @param pageSize
     * @param ship_name
     * @param ship_tel  手机或固定电话
     * @return
     */
    public Page searchForGuest(int page, int pageSize, String ship_name, String ship_tel);

    /**
     * 查询某一用户某一状态下的订单列表
     *
     * @param status
     * @param memberid
     * @return
     */
    public Page listByStatus(int pageNo, int pageSize, int status, long memberid);

    /**
     * 查询某一用户的订单列表
     *
     * @param status
     * @param memberid
     * @return
     */
    public Page listByStatus(int pageNo, int pageSize, long memberid);

    /**
     * 读取某会员某状态的订单列表
     *
     * @param status
     * @param memberid
     * @return
     */
    public List<Order> listByStatus(int status, long memberid);


    /**
     * 查询某一用户的所有订单数
     *
     * @param member_id
     * @return
     */
    public int getMemberOrderNum(long member_id);

    /**
     * @param pageNO页数
     * @param pageSize页面行数
     * @param disabled是否作废0是正常
     * @param sn订单编号
     * @param logi_no物流单号
     * @param uname会员用户名
     * @param ship_name收货人姓名
     * @return
     */
    public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname, String ship_name, int status);

    public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname, String ship_name, int status, Integer paystatus);

    public Page listbyshipid(int pageNo, int pageSize, int status, int shipping_id, String sort, String order);


    public boolean delItem(Integer itemid, Integer itemnum);


    /**
     * 更新付款方式
     *
     * @param orderid
     * @param payid
     * @param paytype
     */
    public void updatePayMethod(int orderid, int payid, String paytype, String payname);


    /**
     * 检测某个货品是否有订单使用
     *
     * @param productid
     * @return
     */
    public boolean checkProInOrder(int productid);


    /**
     * 检测某个货品是否有订单使用
     *
     * @param goodsid
     * @return
     */
    public boolean checkGoodsInOrder(int goodsid);

    public String createSn();

    public List listByOrderIds(Integer[] orderids, String order);

    /**
     * 查询订单列表
     *
     * @param map
     * @param page
     * @param pageSize
     * @param other
     * @return
     * @author xulipeng 2014年5月15日11:18
     */
    public Page listOrder(Map map, int page, int pageSize, String sort, String order);

    /**
     * 保存库房
     *
     * @param orderid
     * @param depotid
     * @author LiFenLong
     */
    public void saveDepot(int orderid, int depotid);

    /**
     * 保存付款方式
     *
     * @param orderId
     * @param paytypeid
     * @author LiFenLong
     */
    public void savePayType(int orderId, int paytypeid);

    /**
     * 保存配送方式
     *
     * @param orederId
     * @param shiptypeid
     * @author LiFenLong
     */
    public void saveShipType(int orederId, int shiptypeid);

    /**
     * 测试用
     *
     * @param order
     * @return
     * @author xulipeng
     */
    public void add(Order order);

    /**
     * 修改配送地区
     *
     * @param ship_provinceid
     * @param ship_cityid
     * @param ship_regionid
     */
    public void saveAddr(int orderId, int ship_provinceid, int ship_cityid, int ship_regionid, String Attr);

    /**
     * 通过订单ID，获得该订单ID下的商品个数
     */

    public Integer getOrderGoodsNum(int order_id);

    /**
     * 获取申请退货订单商品列表
     *
     * @return
     */
    public List getOrderItemDetail(int item_id);

    /**
     * 通过itemid 获取订单项  为了增加is_pack属性
     *
     * @param itemId
     * @return
     */
    public List listGoodsItemsByItemId(Integer itemId);

    /**
     * 根据某一个订单是否是当前登录会员的订单
     *
     * @param sn
     * @param memberid
     * @return
     */
    public boolean getOrderByMemberid(String sn, long memberid);


    /**
     * 获得该会员订单在各个状态的个数
     * whj    2015-08-11
     */
    public Integer orderStatusNum(Integer status);


    /**
     * 获得订单列表
     */
    public List<Map<String, Object>> listForErp1(Map<String, Object> map, String sort, String order);


    /**
     * update 发货状态
     *
     * @return
     */
    public void updateShipStatus(List<Element> items);

    /**
     * 订单简要列表
     *
     * @param pageNo
     * @param pageSize
     * @param status
     * @param member_id
     * @return
     */
    public Page listByStatusForApp(int pageNo, int pageSize, String status, long member_id);

    /**
     * 我的存酒
     *
     * @param memberId
     * @return
     */
    public Page getMystorage(long memberId, int pageNo, int pageSize);

    /**
     * 取酒
     */
    public void getWine(long member_id, Integer order_id);

    /**
     * 取订单商品
     */
    public List<Map<String, Object>> getOrderItemList(int orderId);

    /**
     * 更新订单状态
     * 活动中30分钟不付款 订单作废
     * 普通订单24小时不付款 订单作废
     * 发货15天 没收货 自动确认收货
     */
    public void updateStatus();

    /**
     * 更新所有的id的erp是否已取走的状态, 订单号都是父订单
     *
     * @param orderIdArray
     */
    public void updateErpFlag(Integer[] orderIdArray);

    /**
     * 更新所有的id的erp订单状态
     *
     * @param status
     * @param ids
     */
    public void updateErpStatus(int status, Integer[] ids);

    public void updatePaymentTime(Integer orderid);
    
    public void updatePaymentInfo(Integer orderid, PayCfg payCfg);
}
