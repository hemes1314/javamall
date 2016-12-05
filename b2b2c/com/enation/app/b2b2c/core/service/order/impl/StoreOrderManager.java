package com.enation.app.b2b2c.core.service.order.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.Drools.PromoActivity.DiscountItem;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.impl.CartManager;
import com.enation.app.shop.core.service.impl.MemberManager;
import com.enation.app.shop.core.service.impl.OrderManager;
import com.enation.app.shop.core.service.impl.ProductManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


/**
 * 多店铺订单管理类<br>
 * 负责多店铺订单的创建、查询
 *
 * @author kingapex
 * @version 2.0: 对价格逻辑进行改造
 *          2015年8月21日下午1:49:27
 */
@Component
public class StoreOrderManager extends BaseSupport implements IStoreOrderManager {

    private final static String PC = "PC";
    private final static String APP = "APP";


    private IStoreCartManager storeCartManager;
    @Autowired
    private CartManager cartManager;
    private IDlyTypeManager dlyTypeManager;
    private IPaymentManager paymentManager;
    private OrderPluginBundle orderPluginBundle;
    private IPromotionManager promotionManager;
    private IStoreGoodsManager storeGoodsManager;
    private IStoreMemberManager storeMemberManager;
    @Autowired
    private OrderManager orderManager;
    private CartPluginBundle cartPluginBundle;
    private StoreCartPluginBundle storeCartPluginBundle;
    private MemberManager memberManager;
    private IBonusManager bonusManager;
    private IGoodsManager goodsManager;
    private IStoreManager storeManager;
    private IReceiptManager receiptManager;

    @Autowired
    private CostDownManager costDownManager;

    @Autowired
    private ProductManager productManager;
    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#createOrder(com.enation.app.shop.core.model.Order, java.lang.String, java.lang.String[])
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order createOrder(Order order, String sessionid) {

        storeCartManager.resetCartPrice();
        
        //读取所有的购物项，用于创建主订单
        List<CartItem> cartItemList = this.cartManager.listGoods(sessionid, true);

        //调用核心api计算总订单的价格，商品价：所有商品，商品重量：
        OrderPrice orderPrice = cartManager.countPrice(cartItemList, order.getShipping_id(), "" + order.getRegionid());

        //激发总订单价格事件
        orderPrice = this.cartPluginBundle.coutPrice(orderPrice);
        /*if (!orderPrice.isDiscountValid()) {
            throw new RuntimeException("优惠金额大于应支付价格, 无法使用!");
        }
        if (!orderPrice.isBonusValid()) {
            throw new RuntimeException("红包金额大于应支付价格, 无法使用!");
        }*/
        //判断如果页面传递的支付金额与重新计算的金额不一致。说明购物车发生变化
        if (order.getNeed_pay_money().compareTo(orderPrice.getNeedPayMoney()) != 0) {
            return null;
        }

        //设置订单价格，自动填充好各项价格，商品价格，运费等
        order.setOrderprice(orderPrice);

        //设置订单活动状态冗余 Jeffrey 3-11
        for (CartItem cartItem : cartItemList) {
            if (order.getActivities() > 0)
                break;
            Goods goods = goodsManager.getGoods(cartItem.getGoods_id());
            Product product = productManager.getByGoodId(cartItem.getGoods_id());
            if (goods.getDisabled() == 1 || goods.getMarket_enable() == 0)
                throw new RuntimeException("商品已下架!");
            if (product.getEnable_store() < cartItem.getNum())
                throw new RuntimeException("商品库存不足!");
            if (goods.getIsGroupbuy() == 1)
                order.setActivities(1);
            if (goods.getIsAdvbuy() == 1)
                order.setActivities(1);
            if (goods.getIsFlashbuy() == 1)
                order.setActivities(1);
            if (goods.getIsSecbuy() == 1)
                order.setActivities(1);
            if (goods.getIsCostDown() != null && goods.getIsCostDown() == 1)
                order.setActivities(1);

            //判断该商品是否超出直降限制
            CostDown costDown = costDownManager.getBuyGoodsId(cartItem.getGoods_id());
            if (costDown != null) {
                if (costDown.getGoods_num() == 0) {
                    throw new RuntimeException("此活动商品已售罄！");
                }
                if (costDown.getGoods_num() < cartItem.getNum()) {
                    throw new RuntimeException("超出购买限制，此商品还可以购买" + costDown.getGoods_num() + "件！");
                }
            }

        }

        //调用核心api创建主订单
        Order mainOrder = this.orderManager.add(order, new ArrayList<CartItem>(), sessionid, orderPrice);

        //创建子订单
        this.createChildOrder(mainOrder, sessionid, PC);

        //创建完子订单再清空session
        Integer[] cartIds = new Integer[cartItemList.size()];
        for (int i = 0; i < cartItemList.size(); i++) {
            cartIds[i] = cartItemList.get(i).getId();
        }

        cartManager.clean(sessionid, cartIds);
        StoreCartContainer.cleanSession();
        ThreadContextHolder.getSessionContext().removeAttribute("useBalance");

        //返回主订单
        return mainOrder;
    }

    /**
     * lxl   update  For  App
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Order createOrderForApp(Order order, String sessionid, String cart_id) {

        storeCartManager.resetCartPrice();
        
        //读取所有的购物项，用于创建主订单
        List<CartItem> cartItemList = cartManager.getByCartId(cart_id);

        //调用核心api计算总订单的价格，商品价：所有商品，商品重量：
        OrderPrice orderPrice = cartManager.countPrice(cartItemList, order.getShipping_id(), "" + order.getRegionid());

        //激发总订单价格事件
        orderPrice = this.cartPluginBundle.coutPrice(orderPrice);

        //设置订单价格，自动填充好各项价格，商品价格，运费等
        order.setOrderprice(orderPrice);

        //设置订单活动状态冗余 Jeffrey 3-11
        for (CartItem cartItem : cartItemList) {
            if (order.getActivities() > 0)
                break;
            Goods goods = goodsManager.getGoods(cartItem.getGoods_id());
            Product product = productManager.getByGoodId(cartItem.getGoods_id());
            if (goods.getDisabled() == 1 || goods.getMarket_enable() == 0)
                throw new RuntimeException("商品已下架!");
            if (product.getEnable_store() < cartItem.getNum())
                throw new RuntimeException("商品库存不足!");
            if (goods.getIsGroupbuy() == 1)
                order.setActivities(1);
            if (goods.getIsAdvbuy() == 1)
                order.setActivities(1);
            if (goods.getIsFlashbuy() == 1)
                order.setActivities(1);
            if (goods.getIsSecbuy() == 1)
                order.setActivities(1);
        }

        //设置订单价格，自动填充好各项价格，商品价格，运费等
        order.setOrderprice(orderPrice);

        //调用核心api创建主订单
        Order mainOrder = this.orderManager.add(order, new ArrayList<CartItem>(), sessionid, orderPrice);

        //创建子订单
        this.createChildOrder(mainOrder, sessionid, APP);


        //创建完子订单再清空session
        cartManager.deleteForApp(cart_id);
        StoreCartContainer.cleanSession();

        //返回主订单
        return mainOrder;
    }


    /**
     * 创建店铺子订单
     *
     * @param order       主订单
     * @param sessionid   用户sessionid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void createChildOrder(Order order, String sessionid, String site) {

        //获取以店铺id分类的购物车列表
        List<Map> storeGoodsList = StoreCartContainer.getStoreCartListFromSession();

        int num = 0;
        // 2015/10/26 humaodong
        Double bonusPay = order.getOrderprice().getBonusPay();

       
        //总红包和
        double allRedPackets = 0d;
        //总余额和
        double allBalance = 0d;
        if (null != order.getOrderprice().getBalancePay())
            allBalance = order.getOrderprice().getBalancePay();
        
        int i = 0;
        //以店铺分单位循环购物车列表
        for (Map map : storeGoodsList) {
            ++i;

            //当前店铺的配送方式
            Integer shippingId = (Integer) map.get(StoreCartKeyEnum.shiptypeid.toString());

            //先将主订单的信息copy一份
            StoreOrder storeOrder = this.copyOrder(order);
            //传入父订单,用户创建时候复制状态
            storeOrder.setParentOrder(order);

            //如果copy属性异常，则抛出异常
            if (storeOrder == null) {
                throw new RuntimeException("创建子订单出错，原因为：beanutils copy属性出错。");
            }

            //获取此店铺id
            int store_id = (Integer) map.get(StoreCartKeyEnum.store_id.toString());

            //获取店铺名称
            String store_name = (String) map.get(StoreCartKeyEnum.store_name.toString());

            //设置订单为未结算
            storeOrder.setBill_status(0);

            //设置店铺的id
            storeOrder.setStore_id(store_id);

            int store_type = storeManager.getStore(store_id).getStore_type();

            storeOrder.setSite(site); //站点

            //店铺名称
            storeOrder.setStore_name(store_name);
            //自营店、第三方店
            if (store_type == 1) {
                storeOrder.setStore_type_name("第三方店铺");
            }
            if (store_type == 0) {
                storeOrder.setStore_type_name("自营店铺");
            }

            //配送方式id
            storeOrder.setShipping_id(shippingId);

            //设置父订id
            storeOrder.setParent_id(order.getOrder_id());

            //取得此店铺的购物列表
            List<CartItem> itemlist = (List<CartItem>) map.get(StoreCartKeyEnum.goodslist.toString());

            //调用核心api计算总订单的价格，商品价：所有商品，商品重量：
            OrderPrice orderPrice = (OrderPrice) map.get(StoreCartKeyEnum.storeprice.toString());

            double proportionDiscount = 0d;
            for (CartItem ci : itemlist) {
                DiscountItem discountItem = order.getOrderprice().getPromotionDiscountItemMap().get(ci.getId().intValue());
                if (null == discountItem)
                    continue;
                proportionDiscount += discountItem.getProportion();
            }
            
            //满减
            orderPrice.setPromotionDiscount(proportionDiscount);
            
            //订单总价 商品价格 + 运费 - 满减 - 购物券 
            orderPrice.setOrderPrice(orderPrice.getGoodsBasePrice() + orderPrice.getShippingPrice() - orderPrice.getDiscountPrice() - orderPrice.getPromotionDiscount());
            
            //分摊 - 红包
            double redPackets = 0d;
            if (null != order.getOrderprice().getBonusPay() && order.getOrderprice().getBonusPay() > 0d) {
                if (i != storeGoodsList.size()) {
                    //子订单金额 / 总订单金额 * 红包
                    //四舍五入 到1位小数
                    redPackets = Math.round(orderPrice.getOrderPrice() / order.getOrder_amount() * order.getBonus_pay() * 10) / 10d;
                    //redPackets = Math.round(orderPrice.getOrderPrice() / order.getOrderprice().getOrderPrice() * order.getOrderprice().getBonusPay() * 10) / 10d;
                    //剩余红包金额
                    double surplus = order.getOrderprice().getBonusPay() - allRedPackets;
                    if (surplus < redPackets) {
                        redPackets = surplus;
                        allRedPackets = order.getOrderprice().getBonusPay();
                    } else {
                        allRedPackets += redPackets;
                    }
                } else
                    redPackets = order.getOrderprice().getBonusPay() - allRedPackets;
            }
            if (redPackets < 0) {
                redPackets = 0;
            }
            orderPrice.setBonusPay(redPackets);
            
            //分摊 余额
            double balancePay = 0d;
            if (orderPrice.getOrderPrice() - redPackets > 0)
                balancePay = orderPrice.getOrderPrice() - redPackets;
            if (balancePay > 0) {
                if (allBalance > balancePay) {
                    allBalance = allBalance - balancePay;
                } else {
                    balancePay = allBalance;
                    allBalance = 0d;
                }
            }
            orderPrice.setBalancePay(balancePay);
            
            //应付价格 总价- 红包 - 余额
            orderPrice.setNeedPayMoney(orderPrice.getOrderPrice()
                    - orderPrice.getBalancePay() - orderPrice.getBonusPay());
            
            //设置订单价格，自动填充好各项价格，商品价格，运费等
            storeOrder.setOrderprice(orderPrice);
            
            
            /********************** 2015/10/26 humaodong *********************/
            /* This section has already calculated in OrderPrice, why wrote these code? */
            /*
            if (bonusPay > 0) {
                Double needPay = storeOrder.getNeed_pay_money();
                if (needPay > bonusPay) {
                    storeOrder.setBonus_pay(bonusPay);
                    needPay -= bonusPay;
                    bonusPay = 0.0d;
                } else {
                    storeOrder.setBonus_pay(needPay);
                    bonusPay -= needPay;
                    needPay = 0.0d;
                }
                storeOrder.setNeed_pay_money(needPay);
            }
            if (balancePay > 0) {
                Double needPay = storeOrder.getNeed_pay_money();
                if (needPay > balancePay) {
                    storeOrder.setAdvance_pay(balancePay);
                    needPay -= balancePay;
                    balancePay = 0.0d;
                } else {
                    storeOrder.setAdvance_pay(needPay);
                    balancePay -= needPay;
                    needPay = 0.0d;
                }
                storeOrder.setNeed_pay_money(needPay);
            }
            //*/
            /*****************************************************************/

            //调用订单核心类创建子订单
            this.orderManager.add(storeOrder, itemlist, sessionid, order.getOrderprice());


            //获取优惠券，使用优惠券
            Map<Integer, MemberBonus> bonus = (Map) ThreadContextHolder
                    .getSessionContext().getAttribute(
                            BonusSession.B2B2C_SESSIONKEY);

            if (bonus != null) {
                //查询上边代码保存的生成 sn码的 子订单
                Map queryorder = this.daoSupport.queryForMap("select order_id,sn from es_order where order_id = ?", this.daoSupport.getLastId("es_order"));
                for (Integer sid : bonus.keySet()) {
                    //如果 店铺id 相等
                    if (sid == store_id) {
                        for (Integer bonusId : storeOrder.getBonusIds()) {
                            if (bonusId.intValue() != bonus.get(sid).getBonus_id())
                                continue;
                            bonusManager.use(bonus.get(sid).getBonus_id(),
                                    order.getMember_id(), NumberUtils.toInt(queryorder.get("order_id").toString()),
                                    queryorder.get("sn").toString(), bonus.get(sid).getBonus_type_id());
                            break;
                        }
                    }
                }
            }
            num++;
        }

    }


    /**
     * copy一个订单的属性 生成新的订单
     *
     * @param order 主订单
     * @return 新的子订单
     */
    private StoreOrder copyOrder(Order order) {
        StoreOrder store_order = new StoreOrder();
        try {
            BeanUtils.copyProperties(store_order, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return store_order;
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#storeOrderList(java.lang.Integer, java.lang.Integer, java.util.Map)
     */
    @Override
    public Page storeOrderList(Integer pageNo, Integer pageSize, Integer storeid, Map map) {
        String order_state = String.valueOf(map.get("order_state"));
        String keyword = String.valueOf(map.get("keyword"));
        String buyerName = String.valueOf(map.get("buyerName"));
        String startTime = String.valueOf(map.get("startTime"));
        String endTime = String.valueOf(map.get("endTime"));

        StringBuffer sql = new StringBuffer("select * from es_order o where store_id =" + storeid + " and disabled=0");
        if (!StringUtil.isEmpty(order_state) && !order_state.equals("all")) {
            if (order_state.equals("wait_ship")) { //对待发货的处理
                sql.append(" and ( ( payment_type!='cod' and payment_id!=8  and  status=" + OrderStatus.ORDER_PAY_CONFIRM + ") ");//非货到付款的，要已结算才能发货
                sql.append(" or ( payment_type='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ")) ");//货到付款的，新订单（已确认的）就可以发货
            } else if (order_state.equals("wait_pay")) {
                sql.append(" and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ") ");//非货到付款的，未付款状态的可以结算
                sql.append(" or ( status!=" + OrderStatus.ORDER_NOT_PAY + "  and  pay_status!=" + OrderStatus.PAY_CONFIRM + ")");
                sql.append(" or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status=" + OrderStatus.ORDER_ROG + " )  ) )");//货到付款的要发货或收货后才能结算

            } else if (order_state.equals("wait_rog")) {
                sql.append(" and status=" + OrderStatus.ORDER_SHIP);
            } else {
                sql.append(" and status=" + order_state);
            }
        }
        if (!StringUtil.isEmpty(keyword) && !keyword.equals("null")) {
            sql.append(" AND o.sn like '%" + keyword + "%'");
        }
        if (!StringUtil.isEmpty(buyerName) && !buyerName.equals("null")) {
            sql.append(" AND o.ship_name like '%" + buyerName + "%'");
        }
        if (!StringUtil.isEmpty(startTime) && !startTime.equals("null")) {
            startTime += " 00:00:00";
            sql.append(" AND o.create_time >=" + DateUtil.getDateline(startTime));
        }
        if (!StringUtil.isEmpty(endTime) && !endTime.equals("null")) {
            endTime += " 23:59:59";
            sql.append(" AND o.create_time <=" + DateUtil.getDateline(endTime));
        }
        sql.append(" order by o.create_time desc");
        Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Order.class);

        return rpage;
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#storeOrderList(java.lang.Integer)
     */
    @Override
    public List storeOrderList(Integer parent_id) {
        StringBuffer sql = new StringBuffer("SELECT * from es_order WHERE  disabled=0 AND parent_id=" + parent_id);
        return this.daoSupport.queryForList(sql.toString(), StoreOrder.class);
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#get(java.lang.Integer)
     */
    @Override
    public StoreOrder get(Integer orderId) {
        String sql = "select * from es_order where order_id=?";
        StoreOrder order = (StoreOrder) this.daoSupport.queryForObject(sql, StoreOrder.class, orderId);
        return order;
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#saveShipInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean saveShipInfo(String remark, String ship_day,
                                String ship_name, String ship_tel, String ship_mobile,
                                String ship_zip, int orderid) {
        Order order = this.get(orderid);
        try {
            if (ship_day != null && !StringUtil.isEmpty(ship_day) && !ship_day.equals(order.getShip_day())) {
                this.baseDaoSupport.execute("update order set ship_day=?  where order_id=?", ship_day, orderid);
            }
            if (remark != null && !StringUtil.isEmpty(remark) && !remark.equals("undefined") && !remark.equals(order.getRemark())) {
                this.baseDaoSupport.execute("update order set remark= ?  where order_id=?", remark, orderid);
            }
            if (ship_name != null && !StringUtil.isEmpty(ship_name) && !ship_name.equals(order.getShip_name())) {
                this.baseDaoSupport.execute("update order set ship_name=?  where order_id=?", ship_name, orderid);
            }
            if (ship_tel != null && !StringUtil.isEmpty(ship_tel) && !ship_tel.equals(order.getShip_tel())) {
                this.baseDaoSupport.execute("update order set ship_tel=?  where order_id=?", ship_tel, orderid);
            }
            if (ship_mobile != null && !StringUtil.isEmpty(ship_mobile) && !ship_mobile.equals(order.getShip_mobile())) {
                this.baseDaoSupport.execute("update order set ship_mobile=?  where order_id=?", ship_mobile, orderid);
            }
            if (ship_zip != null && !StringUtil.isEmpty(ship_zip) && !ship_zip.equals(order.getShip_zip())) {
                this.baseDaoSupport.execute("update order set ship_zip=?  where order_id=?", ship_zip, orderid);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#pageOrders(int, int, java.lang.String, java.lang.String)
     */
    @Override
    public Page pageOrders(int pageNo, int pageSize, String status,
                           String keyword) {
        StoreMember member = storeMemberManager.getStoreMember();
        /*
        * 版权：〈版权〉
		* 描述：订单查询显示问题
		* 修改人：Liushuai
		* 修改时间：2015年9月18日18:40:51  
		* 修改内容：sql 查询符合状态的子订单的父订单id，然后根据父订单id查询父订单，最后将父订单和子订单一起放入集合，提供前台显示
		*/
        StringBuilder statusParam = new StringBuilder();


        if (!StringUtil.isEmpty(status)) {
            int statusNumber = -999;
            statusNumber = StringUtil.toInt(status);
            statusParam.append(" AND o.status='" + statusNumber + "'");
        }
        String param1 = "";
        String param2 = "";
        if (!StringUtil.isEmpty(keyword)) {
            param1 = " AND o.sn like '%" + keyword + "%'";
            param2 = " AND o.order_id in (SELECT i.order_id FROM es_order_items i LEFT JOIN es_order o ON i.order_id=o.order_id WHERE o.member_id='" + member.getMember_id() + "' "
                    + " AND (i.name like '%" + keyword + "%' OR o.sn LIKE '%" + keyword + "%'))";
        }

        String sql = "";
        //查询所有订单（只查询父订单）
        if (StringUtils.isBlank(keyword) && StringUtils.isBlank(status)) {
            sql = "select o.* from ES_ORDER o where o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID is NULL ORDER BY o.order_id DESC ";
        } else if (StringUtils.isBlank(keyword) && StringUtils.isNotBlank(status)) //查询子订单中状态
        {
            sql = "SELECT DISTINCT eo.order_id,eo.sn,eo.member_id,eo.status,eo.pay_status,eo.ship_status,eo.shipping_type,eo.payment_id,eo.payment_name,eo.paymoney,eo.create_time,eo.goods_amount," +
                    "eo.order_amount,eo.goods_num,eo.need_pay_money,eo.store_id,eo.parent_id,eo.store_name FROM ES_ORDER o,ES_ORDER eo WHERE o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID = eo.order_id " + statusParam.toString() + " ORDER BY eo.order_id DESC";

        } else if (StringUtils.isNotBlank(keyword) && StringUtils.isBlank(status)) {

            sql = "select o.* from ES_ORDER o where o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID is NULL " + param1 +
                    " Union All (SELECT eo.* FROM ES_ORDER o,ES_ORDER eo WHERE o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID = eo.order_id " + param2 + ")";
        }

        //获取订单总个数
        Integer totalSize = this.getTotalSize(status, keyword, member);
        // sql.append(" AND parent_id is  null ORDER BY order_id DESC");
        List<Map> list = this.daoSupport.queryForListPage(sql, pageNo, pageSize);

        String cSql = "";
        //存储所有子订单的结果集
        List<List<Map>> tList = new ArrayList<List<Map>>();
        // 	循环查询所有的子订单
        for (Map m : list) {
            cSql = "select * from es_order where parent_id = '" + m.get("order_id") + "'";
            tList.add(this.daoSupport.queryForListPage(cSql.toString(), 1, 999));
        }
        //循环将所有查询到的结果集 放入要显示的集合 后边显示逻辑不变
        for (List<Map> tlist : tList) {
            list.addAll(tlist);
        }

        List newList = new ArrayList();

        //循环订单列表
        for (Map map : list) {
            //判断是否为父订单
            if (map.get("parent_id") == null || map.get("parent_id").toString().equals("0")) {
                //存放父订单
                Map parent = new HashMap();
                parent.put("parent", map);
                String paymentType = (String) map.get("payment_type");
                paymentType = StringUtils.trimToEmpty(paymentType);
                Double needPay = (Double) map.get("need_pay_money");
                if (needPay == null) needPay = 0d;
                boolean notPay = OrderStatus.ORDER_NOT_PAY == (int) map.get("status");
                List child = new ArrayList();
                //寻找子订单并且添加子订单
                for (Map map1 : list) {
                    if (map1.get("parent_id") != null && NumberUtils.toInt(map1.get("parent_id").toString()) == NumberUtils.toInt(map.get("order_id").toString())) {
                        child.add(map1);
                        if (OrderStatus.ORDER_NOT_PAY != (int) map1.get("status")) notPay = false;
                    }
                }
                parent.put("can_pay_all", !paymentType.equals("cod") && needPay > 0 && child.size() > 1 && notPay ? 1 : 0);
                parent.put("child", child);
                newList.add(parent);
            }
        }
        Page page = new Page(0, totalSize, 20, newList);
        return page;
    }


    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#pageOrders(int, int, java.lang.String, java.lang.String)
     */
    @Override
    public Page pageChildOrders(int pageNo, int pageSize, String status,
                                String keyword) {
        StoreMember member = storeMemberManager.getStoreMember();

        StringBuffer sql = new StringBuffer("select * from " + this.getTableName("order") + " where member_id = '" + member.getMember_id() + "' and disabled=0");
        if (!StringUtil.isEmpty(status)) {
            int statusNumber = -999;
            statusNumber = StringUtil.toInt(status);
            //等待付款的订单 按付款状态查询
            if (statusNumber == 0) {
                sql.append(" AND status!=" + OrderStatus.ORDER_CANCELLATION + " AND pay_status=" + OrderStatus.PAY_NO);
            } else {
                sql.append(" AND status='" + statusNumber + "'");
            }
        }
        if (!StringUtil.isEmpty(keyword)) {
            sql.append(" AND order_id in (SELECT i.order_id FROM " + this.getTableName("order_items") + " i LEFT JOIN " + this.getTableName("order") + " o ON i.order_id=o.order_id WHERE o.member_id='" + member.getMember_id() + "' AND i.name like '%" + keyword + "%')");
        }
        sql.append(" AND parent_id is NOT NULL order by create_time desc");
        Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Order.class);
        return rpage;
    }

    public Integer getTotalSize(String status, String keyword, StoreMember member) {
        StringBuilder statusParam = new StringBuilder();
        if (!StringUtil.isEmpty(status)) {
            int statusNumber = -999;
            statusNumber = StringUtil.toInt(status);
            statusParam.append(" AND o.status='" + statusNumber + "'");
        }
        String param1 = "";
        String param2 = "";
        if (!StringUtil.isEmpty(keyword)) {
            param1 = " AND o.sn like '%" + keyword + "%'";
            param2 = " AND o.order_id in (SELECT i.order_id FROM es_order_items i LEFT JOIN es_order o ON i.order_id=o.order_id WHERE o.member_id='" + member.getMember_id() + "' "
                    + "AND (i.name like '%" + keyword + "%' OR o.sn LIKE '%" + keyword + "%'))";
        }

        String sql = "";
        //查询所有订单（只查询父订单）
        if (StringUtils.isBlank(keyword) && StringUtils.isBlank(status)) {
            sql = "select count(1) from ES_ORDER o where o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID is NULL ORDER BY o.order_id DESC ";
        } else if (StringUtils.isBlank(keyword) && StringUtils.isNotBlank(status)) //查询子订单中状态
        {
            sql = "SELECT count(DISTINCT eo.order_id) FROM ES_ORDER o,ES_ORDER eo WHERE o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID = eo.order_id " + statusParam.toString();

        } else if (StringUtils.isNotBlank(keyword) && StringUtils.isBlank(status)) {

            sql = "select sum(s) from ( select count(1) as s from ES_ORDER o where o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID is NULL " + param1 +
                    "union (SELECT count(1) as s FROM ES_ORDER o2,ES_ORDER o WHERE o.member_id = " + member.getMember_id() + " and o.disabled=0 and o.PARENT_ID = o2.order_id " + param2 + ") )";
        }
        return this.daoSupport.queryForInt(sql);

    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#getStoreOrderNum(int)
     */
    @Override
    public int getStoreOrderNum(int struts) {
        StoreMember member = storeMemberManager.getStoreMember();
        String sql = "select count(order_id) from es_order o where o.store_id =" + member.getStore_id() + " and o.disabled=0";
        if (struts != -999) {
            //add by Tension 查询代发货的商品
            if (struts == 2) {
                sql = sql + " and ( ( payment_type!='cod' and payment_id!=8  and  status=" + OrderStatus.ORDER_PAY_CONFIRM + ") ";
                sql = sql + "or ( payment_type='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + "))";
            } else {
                sql = sql + " AND o.status=" + struts;
            }
        }
        return this.daoSupport.queryForInt(sql);
    }

    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#get(java.lang.String)
     */
    @Override
    public StoreOrder get(String ordersn) {
        String sql = "select * from es_order where sn='" + ordersn + "'";
        StoreOrder order = (StoreOrder) this.baseDaoSupport.queryForObject(sql, StoreOrder.class);
        return order;
    }

    @Override
    public Page listOrder(Map map, int page, int pageSize, String other, String order) {
        String sql = createTempSql(map, other, order);
        Page webPage = this.baseDaoSupport.queryForPage(sql, page, pageSize);

        //统一获取支付方式,之前的sql太复杂了,一次性获取有难度,改为分两次, 一次性找出所有的父订单,统计支付方式放入map,然后倒腾回子订单map
        if (webPage.getResult() != null) {
            List list = (List)webPage.getResult();
            if (!list.isEmpty()) {
                Set<Integer> pidSet = new HashSet<>();
                for (Object o : list) {
                    Map m = (Map) o;
                    Integer v = (Integer)m.get("parent_id");
                    if (v != null) {
                        pidSet.add(v);
                    }
                }
//                Integer[] array = new Integer[pidSet.size()];
//                int i = 0;
//                for (Integer pid : pidSet) {
//                    array[i++] = pid;
//                }
//                String ids = StringUtil.arrayToString(array, ",");
                String ids = StringUtil.arrayToString(pidSet.toArray(), ",");
                //String sql1 = "select order_id,need_pay_money,payment_name,advance_pay,bonus_pay where order_id in (" + ids + ")";
                String sql1 = "select * from es_order where order_id in (" + ids + ")";
                List<Order> parentOrders = baseDaoSupport.queryForList(sql1, Order.class);
                Map<Integer, String> paymentMethodMap = new HashedMap();
                Map<Integer, String> parentSnMap = new HashMap<Integer, String>();
                for (Order po : parentOrders) {
                    paymentMethodMap.put(po.getOrder_id(), po.getPaymentMethod());
                    parentSnMap.put(po.getOrder_id(), po.getSn());
                }
                for (Object o : list) {
                    Map m = (Map) o;
                    Integer v = (Integer)m.get("parent_id");
                    if (v != null) {
                        //m.put("paymentMethod", paymentMethodMap.get(v));
                        //直接放入这里,不用修改页面了
                        m.put("payment_name", paymentMethodMap.get(v));
                        m.put("parent_sn", parentSnMap.get(v));
                    }
                }

            }
        }

        return webPage;
    }

    private String createTempSql(Map map, String other, String order) {

        Integer stype = (Integer) map.get("stype");
        String keyword = (String) map.get("keyword");
        String orderstate = (String) map.get("order_state");//订单状态
        String start_time = (String) map.get("start_time");
        String end_time = (String) map.get("end_time");
        Integer status = (Integer) map.get("status");
        String sn = (String) map.get("sn");
        String ship_name = (String) map.get("ship_name");
        Integer paystatus = (Integer) map.get("paystatus");
        Integer shipstatus = (Integer) map.get("shipstatus");
        Integer shipping_type = (Integer) map.get("shipping_type");
        Integer payment_id = (Integer) map.get("payment_id");
        String complete = (String) map.get("complete");
        String store_name = (String) map.get("store_name");
        Integer store_id = (Integer) map.get("store_id");
        StringBuilder sql = new StringBuilder();
        sql.append("select * from order where disabled=0 and parent_id is NOT NULL ");

        if (stype != null && keyword != null) {
            if (stype == 0) {
                sql.append(" and (sn like '%" + keyword + "%'");
                sql.append(" or parent_id in(");
                //这里加入了子查询
                sql.append("select order_id from es_order where sn like '%"+keyword+"'");
                sql.append(")");
                sql.append(" or ship_name like '%" + keyword + "%')");
            }
        }

        if (status != null) {
            sql.append("and status=" + status);
        }

        if (StringUtils.isNotBlank(sn)) {
            sql.append(" and sn like '%" + sn + "%'");
        }

        if (StringUtils.isNotBlank(ship_name)) {
            sql.append(" and ship_name like '" + ship_name + "'");
        }

        if (paystatus != null) {
            sql.append(" and pay_status=" + paystatus);
        }

        if (shipstatus != null) {
            sql.append(" and ship_status=" + shipstatus);
        }

        if (shipping_type != null) {
            sql.append(" and shipping_id=" + shipping_type);
        }

        if (payment_id != null) {
            sql.append(" and payment_id=" + payment_id);
        }

        if (StringUtils.isNotBlank(start_time)) {
            sql.append(" and create_time>=" + getDateline(start_time + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(end_time)) {
            sql.append(" and create_time<=" + getDateline(end_time + " 23:59:59"));
        }
        
        //modify by linyang 去掉别名 o ，源程序是抄过来的
        if (StringUtils.isNotBlank(orderstate)) {
            if (orderstate.equals("wait_ship")) { //对待发货的处理
                sql.append(" and ( ( payment_type!='cod' and status=" + OrderStatus.ORDER_PAY_CONFIRM + ") ");//非货到付款的，要已结算才能发货
                sql.append(" or ( payment_type='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ")) ");//货到付款的，新订单（已确认的）就可以发货
            } else if (orderstate.equals("wait_pay")) {
                sql.append(" and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ") ");//非货到付款的，未付款状态的可以结算
                sql.append(" or ( status!=" + OrderStatus.ORDER_NOT_PAY + "  and  pay_status!=" + OrderStatus.PAY_CONFIRM + ")");
                sql.append(" or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status=" + OrderStatus.ORDER_ROG + " )  ) )");//货到付款的要发货或收货后才能结算
            } else if (orderstate.equals("wait_rog")) {
                sql.append(" and status=" + OrderStatus.ORDER_SHIP);
            } else {
                sql.append(" and status=" + orderstate);
            }
        }

        if (StringUtils.isNotBlank(complete)) {
            sql.append(" and status=" + OrderStatus.ORDER_COMPLETE);
        }
        if (StringUtils.isNotBlank(store_name)) {
            sql.append(" and store_id in(select store_id from es_store where store_name like '%" + store_name + "%')");
        }
        //modify by linyang 去掉别名 o ，源程序是抄过来的
        if (store_id != null) {
            sql.append(" and store_id=" + store_id);
        }
        sql.append(" ORDER BY " + other + " " + order);

        return sql.toString();
    }
    
    @Override
    public Map getStatusJson() {
        Map orderStatus = new HashMap();

        orderStatus.put("" + OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
        //orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
        orderStatus.put("" + OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
        orderStatus.put("" + OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
        orderStatus.put("" + OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
        orderStatus.put("" + OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
        orderStatus.put("" + OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
        orderStatus.put("" + OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
        orderStatus.put("" + OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
        orderStatus.put("" + OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
        orderStatus.put("" + OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));

        return orderStatus;
    }

    @Override
    public Map getpPayStatusJson() {
        Map pmap = new HashMap();
        pmap.put("" + OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
        //	pmap.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
        pmap.put("" + OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
        pmap.put("" + OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
        pmap.put("" + OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));

        return pmap;
    }

    @Override
    public Map getShipJson() {
        Map map = new HashMap();
        map.put("" + OrderStatus.SHIP_ALLOCATION_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_NO));
        map.put("" + OrderStatus.SHIP_ALLOCATION_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_YES));
        map.put("" + OrderStatus.SHIP_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_NO));
        map.put("" + OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
        map.put("" + OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
        map.put("" + OrderStatus.SHIP_PARTIAL_SHIPED, OrderStatus.getShipStatusText(OrderStatus.SHIP_PARTIAL_SHIPED));
        map.put("" + OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
        map.put("" + OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
        map.put("" + OrderStatus.SHIP_CHANED, OrderStatus.getShipStatusText(OrderStatus.SHIP_CHANED));
        map.put("" + OrderStatus.SHIP_ROG, OrderStatus.getShipStatusText(OrderStatus.SHIP_ROG));
        return map;
    }

    //set  get

    public Integer orderStatusNum(Integer status) {
        StoreMember member = storeMemberManager.getStoreMember();
        if (status == 99) {
            String sql = "select count(0) from es_order where member_id=? and parent_id is not null";
            return this.baseDaoSupport.queryForInt(sql, member.getMember_id());
        } else {
            String sql = "select count(0) from es_order where status =? and member_id=? and parent_id is not null";
            return this.baseDaoSupport.queryForInt(sql, status, member.getMember_id());
        }

    }

    @Override
    public Integer getStoreGoodsNum(int store_id) {
        String sql = "select count(0) from goods where store_id=?";
        return this.baseDaoSupport.queryForInt(sql, store_id);
    }


    @Override
    public void saveShipNo(Integer[] order_id, Integer logi_id, String logi_name, String shipNo) {
        Map map = new HashMap();
        map.put("ship_no", shipNo);
        map.put("logi_id", logi_id);
        map.put("logi_name", logi_name);
        this.daoSupport.update("es_order", map, "order_id=" + order_id[0]);
    }

    @Override
    public Map censusState() {
        // 构造一个返回值Map，并将其初始化：各种订单状态的值皆为0
        Map<String, Integer> stateMap = new HashMap<String, Integer>(7);
        String[] states = {"cancel_ship", "cancel_pay", "pay", "ship", "complete", "allocation_yes"};
        for (String s : states) {
            stateMap.put(s, 0);
        }

        // 分组查询、统计订单状态
        String sql = "select count(0) num,status  from es_order where disabled = 0 AND parent_id is NOT NULL group by status";
        List<Map<String, Integer>> list = this.daoSupport.queryForList(sql,
                new RowMapper() {
                    public Object mapRow(ResultSet rs, int arg1) throws SQLException {
                        Map<String, Integer> map = new HashMap<String, Integer>();
                        map.put("status", rs.getInt("status"));
                        map.put("num", rs.getInt("num"));
                        return map;
                    }
                });
//
//				// 将list转为map
        for (Map<String, Integer> state : list) {
            stateMap.put(this.getStateString(state.get("status")), state.get("num"));
        }

        sql = "select count(0) num  from es_order where disabled = 0  and status=0 AND parent_id is NOT NULL ";
        int count = this.daoSupport.queryForInt(sql);
        stateMap.put("wait", 0);

        sql = "select count(0) num  from es_order where disabled = 0  AND parent_id is NOT NULL ";
        sql += " and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ") ";//非货到付款的，未付款状态的可以结算
        sql += " or ( status!=" + OrderStatus.ORDER_NOT_PAY + "  and  pay_status!=" + OrderStatus.PAY_CONFIRM + ")";
        sql += " or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status=" + OrderStatus.ORDER_ROG + " )  ) )";//货到付款的要发货或收货后才能结算
        count = this.daoSupport.queryForInt(sql);
        stateMap.put("not_pay", count);

        sql = "select count(0) from es_order where disabled=0  and ( ( payment_type!='cod' and  status=2)  or ( payment_type='cod' and  status=0)) AND parent_id is NOT NULL ";
        count = this.daoSupport.queryForInt(sql);
        stateMap.put("allocation_yes", count);

        return stateMap;
    }

    /**
     * 根据订单状态值获取状态字串，如果状态值不在范围内反回null。
     *
     * @param state
     * @return
     */
    private String getStateString(Integer state) {
        String str = null;
        switch (state.intValue()) {
            case -2:
                str = "cancel_ship";
                break;
            case -1:
                str = "cancel_pay";
                break;
            case 1:
                str = "pay";
                break;
            case 2:
                str = "ship";
                break;
            case 4:
                str = "allocation_yes";
                break;
            case 7:
                str = "complete";
                break;
            default:
                str = null;
                break;
        }
        return str;
    }

    /*
     * add by lin yang
     */
    public static long getDateline(String date) {
        return (long) (toDate(date, "yyyy-MM-dd H:m:s").getTime() / 1000);
    }

    /**
     * 将一个字符串转换成日期格式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date toDate(String date, String pattern) {
        if (("" + date).equals("")) {
            return null;
        }
        if (pattern == null) {
            pattern = "yyyy-MM-dd H:m:s";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date newDate = new Date();
        try {
            newDate = sdf.parse(date);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newDate;
    }

    @Override
    public Map<Order, List<StoreOrder>> listForErp(int max) {
        Map<Order, List<StoreOrder>> map = new LinkedHashMap<>();
        //然后挨个提取吧,愁人. 一次性提取恐怕麻烦太大sql没法写
        //提取所有的,已支付未提取的主订单
        String sql = "select * from es_order where parent_id is null and disabled = 0 and is_erp_process = 0 and status !=" + OrderStatus.ORDER_NOT_PAY + " order by create_time asc";
        sql = baseDaoSupport.buildPageSql(sql, 1, max);
        List<Order> mainOrderList = baseDaoSupport.queryForList(sql, Order.class);
        if (mainOrderList == null || mainOrderList.isEmpty()) {
            return map;
        }

        for (Order mo : mainOrderList) {

            //获取对应的各种参数
            List<StoreOrder> soList = baseDaoSupport.queryForList("select * from es_order where disabled = 0 and parent_id =? and store_type_name='自营店铺'", StoreOrder.class, mo.getOrder_id());
            for (StoreOrder so : soList) {
                //取items
                List<OrderItem> items = baseDaoSupport.queryForList("select * from es_order_items where order_id=? order by item_id asc", OrderItem.class, so.getOrder_id());
                so.setOrderItemList(items);
                Receipt receipt = receiptManager.getByOrderid(so.getOrder_id());
                so.setReceipt(receipt);
            }
            if(soList!=null && soList.size()>0){
                map.put(mo, soList);
            }else{
                this.daoSupport.execute("update es_order set is_erp_process = 1 where order_id = ?", mo.getOrder_id());
            }
        }
        return map;
    }

    public Map<Integer, Double> getProductWeightMap(Integer... productIds) {
        Map<Integer, Double> map = new HashMap<>();
        if (productIds == null || productIds.length == 0) {
            return map;
        }

        String ids = StringUtil.arrayToString(productIds, ",");
        String sql = "select product_id,weight from ES_product where product_id in (" + ids + ")";
        List<Map> list = baseDaoSupport.queryForList(sql);
        for (Map m : list) {
            map.put((Integer) m.get("product_id"), (Double) m.get("weight"));
        }
        return map;
    }

    @Override
    public List<OrderItem> getOmsRefundOrderItems(Order so) {
        //取items
        List<OrderItem> items = baseDaoSupport.queryForList("select * from es_order_items where order_id=? order by item_id asc", OrderItem.class, so.getOrder_id());
        so.setOrderItemList(items);

        return items;
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

    public OrderPluginBundle getOrderPluginBundle() {
        return orderPluginBundle;
    }

    public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
        this.orderPluginBundle = orderPluginBundle;
    }

    public IPromotionManager getPromotionManager() {
        return promotionManager;
    }

    public void setPromotionManager(IPromotionManager promotionManager) {
        this.promotionManager = promotionManager;
    }

    public IStoreGoodsManager getStoreGoodsManager() {
        return storeGoodsManager;
    }

    public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
        this.storeGoodsManager = storeGoodsManager;
    }

    public IStoreCartManager getStoreCartManager() {
        return storeCartManager;
    }

    public void setStoreCartManager(IStoreCartManager storeCartManager) {
        this.storeCartManager = storeCartManager;
    }

    public IStoreMemberManager getStoreMemberManager() {
        return storeMemberManager;
    }

    public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
        this.storeMemberManager = storeMemberManager;
    }

    public CartPluginBundle getCartPluginBundle() {
        return cartPluginBundle;
    }


    public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
        this.cartPluginBundle = cartPluginBundle;
    }


    public StoreCartPluginBundle getStoreCartPluginBundle() {
        return storeCartPluginBundle;
    }


    public void setStoreCartPluginBundle(StoreCartPluginBundle storeCartPluginBundle) {
        this.storeCartPluginBundle = storeCartPluginBundle;
    }


    public MemberManager getMemberManager() {
        return memberManager;
    }


    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public IBonusManager getBonusManager() {
        return bonusManager;
    }

    public void setBonusManager(IBonusManager bonusManager) {
        this.bonusManager = bonusManager;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    public void setStoreManager(IStoreManager storeManager) {
        this.storeManager = storeManager;
    }

    public IReceiptManager getReceiptManager() {
        return receiptManager;
    }

    public void setReceiptManager(IReceiptManager receiptManager) {
        this.receiptManager = receiptManager;
    }

}

