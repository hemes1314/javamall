package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.component.plugin.goods.StoreGoodsCommentsBundle;
import com.enation.app.b2b2c.core.model.Drools.PromoActivity.DiscountItem;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.order.StoreSellBackList;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.store.impl.StoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderAllocationManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.mobile.model.ApiOrderList;
import com.enation.app.shop.mobile.service.impl.ErpManager;
import com.enation.app.shop.mobile.util.OrderUtils;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单管理
 *
 * @author kingapex 2010-4-6上午11:16:01
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OrderManager extends BaseSupport implements IOrderManager {
    private Log log = LogFactory.getLog(OrderManager.class);
    private ICartManager cartManager;
    private IDlyTypeManager dlyTypeManager;
    private IPaymentManager paymentManager;

    private IPromotionManager promotionManager;
    private OrderPluginBundle orderPluginBundle;
    private IPermissionManager permissionManager;
    private IAdminUserManager adminUserManager;
    private IRoleManager roleManager;
    private IGoodsManager goodsManager;
    private IMemberManager memberManager;
    private IOrderAllocationManager orderAllocationManager;
    private IDepotManager depotManager;
    private CartPluginBundle cartPluginBundle;
    private ProductManager productManager;
    private ErpManager erpManager;
    private IStoreMemberManager storeMemberManager;
    private IStoreOrderManager storeOrderManager;
    private ISellBackManager sellBackManager;
    private StoreGoodsCommentsBundle storeGoodsCommentsBundle;
    private ICache iCache;
    private ActivityGoodsManager activityGoodsManager;

    public ISellBackManager getSellBackManager() {
        return sellBackManager;
    }



    public ICache getiCache() {
        return iCache;
    }

    public void setiCache(ICache iCache) {
        this.iCache = iCache;
    }



    public void setSellBackManager(ISellBackManager sellBackManager) {
        this.sellBackManager = sellBackManager;
    }
    public IStoreOrderManager getStoreOrderManager() {
        return storeOrderManager;
    }

    public StoreGoodsCommentsBundle getStoreGoodsCommentsBundle() {
        return storeGoodsCommentsBundle;
    }

    public void setStoreGoodsCommentsBundle(StoreGoodsCommentsBundle storeGoodsCommentsBundle) {
        this.storeGoodsCommentsBundle = storeGoodsCommentsBundle;
    }

    public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
        this.storeOrderManager = storeOrderManager;
    }

    public IStoreMemberManager getStoreMemberManager() {
        return storeMemberManager;
    }

    
    public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
        this.storeMemberManager = storeMemberManager;
    }

    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }

    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }

    @Autowired
    private StoreManager storeManager;

    public IOrderAllocationManager getOrderAllocationManager() {
        return orderAllocationManager;
    }

    public void setOrderAllocationManager(
            IOrderAllocationManager orderAllocationManager) {
        this.orderAllocationManager = orderAllocationManager;
    }

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }

    public IDepotManager getDepotManager() {
        return depotManager;
    }

    public void setDepotManager(IDepotManager depotManager) {
        this.depotManager = depotManager;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void savePrice(double price, int orderid) {
        Order order = this.get(orderid);
        double amount = order.getOrder_amount();
        // double discount= amount-price;
        double discount = CurrencyUtil.sub(amount, price);
        this.baseDaoSupport.execute(
                "update order set order_amount=?,need_pay_money=? where order_id=?", price, price,
                orderid);
        //修改收款单价格
        String sql = "update es_payment_logs set money=? where order_id=?";
        this.daoSupport.execute(sql, price, orderid);

        this.baseDaoSupport.execute(
                "update order set discount=discount+? where order_id=?",
                discount, orderid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public double saveShipmoney(double shipmoney, int orderid) {
        Order order = this.get(orderid);
        double currshipamount = order.getShipping_amount();
        // double discount= amount-price;
        double shortship = CurrencyUtil.sub(shipmoney, currshipamount);
        double discount = CurrencyUtil.sub(currshipamount, shipmoney);
        //2014-9-18 配送费用修改 @author LiFenLong
        this.baseDaoSupport.execute(
                "update order set order_amount=order_amount+?,need_pay_money=need_pay_money+?,shipping_amount=?,discount=discount+? where order_id=?", shortship, shortship, shipmoney, discount,
                orderid);
        //2014-9-12 LiFenLong 修改配送金额同时修改收款单
        this.daoSupport.execute("update es_payment_logs set money=money+? where order_id=?", shortship, orderid);
        return this.get(orderid).getOrder_amount();
    }

    /**
     * 记录订单操作日志
     *
     * @param order_id
     * @param message
     * @param op_id
     * @param op_name
     */
    public void log(Integer order_id, String message, Long op_id, String op_name) {
        OrderLog orderLog = new OrderLog();
        orderLog.setMessage(message);
        orderLog.setOp_id(op_id);
        orderLog.setOp_name(op_name);
        orderLog.setOp_time(com.enation.framework.util.DateUtil.getDateline());
        orderLog.setOrder_id(order_id);
        this.baseDaoSupport.insert("order_log", orderLog);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Order add(Order order, List<CartItem> itemList, String sessionid, OrderPrice mainOrderPrice) {
        String opname = "游客";

        if (order == null)
            throw new RuntimeException("error: order is null");

        /************************** 用户信息 ****************************/
        Member member = UserConext.getCurrentMember();


        /*Member data_member = memberManager.get(member.getMember_id());
        //判断用户数据是否被手动删除
        if (data_member == null) {
            throw new RuntimeException("创建订单失败，当前用户不存在，请联系平台管理员！");
        }*/


        // 非匿名购买
        if (member != null) {
            order.setMember_id(member.getMember_id());
            opname = member.getUname();
        }


        // 配送方式名称
        DlyType dlyType = new DlyType();
        if (dlyType != null && order.getShipping_id() != 0) {
            dlyType = dlyTypeManager.getDlyTypeById(order.getShipping_id());
        } else {
            dlyType.setName("");
        }

        if (dlyType == null) {
            throw new RuntimeException("dly");
        }
        order.setShipping_type(dlyType.getName());

        /************ 支付方式价格及名称 ************************/
        PayCfg payCfg = this.paymentManager.get(order.getPayment_id());
        //此方法实现体为空注释掉
        //order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
        order.setPayment_name(payCfg.getName());

        order.setPayment_type(payCfg.getType());

        /************ 创建订单 ************************/
        order.setCreate_time(com.enation.framework.util.DateUtil.getDateline());

        order.setSn(this.createSn());
        order.setStatus(OrderStatus.ORDER_NOT_CONFIRM);
        order.setDisabled(0);
        order.setPay_status(OrderStatus.PAY_NO);
        order.setShip_status(OrderStatus.SHIP_NO);
        order.setOrderStatus("订单已生效");

        //给订单添加仓库 ------仓库为默认仓库
        Integer depotId = this.baseDaoSupport.queryForInt("select id from depot where choose=1");
        order.setDepotid(depotId);
        /************ 写入订单货物列表 ************************/

        /**检测商品库存  Start**/
        boolean result = true;    //用于判断购买量是否超出库存
        for (CartItem item : itemList) {
            int productId = item.getProduct_id();
            Product product = productManager.get(productId);
            int enableStore = product.getEnable_store();
            int itemNum = item.getNum();
            if (itemNum > enableStore) {
                result = false;
                break;
            }
        }
        if (!result) {
            throw new RuntimeException("创建订单失败，您购买的商品库存不足");
        }
        /**检测商品库存  End**/


        /***************** 2015/10/15 humaodong ***********************/
        //TODO: 不明白 子订单如何处理？？？  暂时先只针对子订单进行支付
        Double money = order.getAdvance_pay();
        if (money != null && money > 0 && itemList.size() != 0) {
            AdvanceLogs log = memberManager.pay(member.getMember_id(), money, 0, order.getSn(), "订单");
            order.setAdvance_pay(log.getExport_advance());
            order.setVirtual_pay(log.getExport_virtual());
            Double need_pay_money = order.getNeed_pay_money();

            //使用余额
            if ((need_pay_money != null && need_pay_money < 0.01) || (need_pay_money != null && Math.abs(need_pay_money - money) < 0.01)) {
                order.setStatus(OrderStatus.ORDER_PAY_CONFIRM);
                order.setPay_status(OrderStatus.PAY_CONFIRM);
                order.setPayment_time(DateUtil.getDateline());
            }
        }
        /******************** end of update ***************************/

        //创建时候复制父订单状态,比如余额支付的,则是已经支付
        Order mainOrder = order.getParentOrder();
        if (mainOrder != null) {
            order.setPay_status(mainOrder.getPay_status());
            order.setStatus(mainOrder.getStatus());
            order.setPayment_time(mainOrder.getPayment_time()); 
        }

        this.orderPluginBundle.onBeforeCreate(order, itemList, sessionid);
        this.baseDaoSupport.insert("order", order);

//		if (itemList.isEmpty() )
//			throw new RuntimeException("创建订单失败，购物车为空");

        Integer orderId = this.baseDaoSupport.getLastId("order");

        order.setOrder_id(orderId);

        if (itemList.size() > 0 && order instanceof StoreOrder)
            this.saveGoodsItem(itemList, (StoreOrder) order, mainOrderPrice);


        /************ 写入订单日志 ************************/
        OrderLog log = new OrderLog();
        log.setMessage("订单创建");
        log.setOp_name(opname);
        log.setOrder_id(orderId);
        this.addLog(log);

        this.orderPluginBundle.onAfterCreate(order, itemList, sessionid);

        //因为在orderFlowManager中已经注入了orderManager，不能在这里直接注入
        //将来更好的办法是将订单创建移到orderFlowManager中
        //下单则自动改为已确认
        IOrderFlowManager flowManager = SpringContextHolder.getBean("orderFlowManager");

        if (order.getStatus() == OrderStatus.ORDER_PAY_CONFIRM) {
            flowManager.payConfirm(orderId);
        } else {
            flowManager.confirmOrder(orderId);
        }



        //只有b2c产品清空session
        if (EopSetting.PRODUCT.equals("b2c")) {
            cartManager.clean(sessionid);
        }
//		HttpCacheManager.sessionChange();

        return order;
    }

    /**
     * 添加订单日志
     *
     * @param log
     */
    private void addLog(OrderLog log) {
        log.setOp_time(com.enation.framework.util.DateUtil.getDateline());
        this.baseDaoSupport.insert("order_log", log);
    }

    /**
     * 保存商品订单项
     *
     * @param itemList
     * @param order_id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void saveGoodsItem(List<CartItem> itemList, StoreOrder order, OrderPrice mainOrderPrice) {

        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Integer order_id = order.getOrder_id();
        
        Map<Integer, DiscountItem> discountItemMap = mainOrderPrice.getPromotionDiscountItemMap();
        Map<Integer, Double> storeShippingMap = mainOrderPrice.getStoreShippingMap();
        Map<Integer, Double> storeCouponMap = mainOrderPrice.getStoreCouponMap();
        //每个店铺可以使用优惠劵总金额
        Map<Integer, Double> storeUseCouponPrice = mainOrderPrice.getStoreUseCouponPrice();

        //每个店铺可以使用优惠劵总金额
        Map<Integer, Integer> storeUseCouponConut = mainOrderPrice.getStoreUseCouponCount();
        
        Double otherRedPacketsProportion = 0d;

        Store s = storeManager.getStore(order.getStore_id());

        //店铺 总价格
        Double storeTotalPrice = 0d;
        for (int i = 0; i < itemList.size(); i++) {
            CartItem cartItem = (CartItem) itemList.get(i);
            storeTotalPrice += cartItem.getPrice() * cartItem.getNum();
        }

        //用于特殊处理 最后一个商品的 分摊
        double otherShippingPrice = 0d;
        double otherCouponPrice = 0d;
        
        //子订单佣金
        Double childOrderCommission = 0d;
        
        for (int i = 0; i < itemList.size(); i++) {

            OrderItem orderItem = new OrderItem();

            CartItem cartItem = (CartItem) itemList.get(i);
            orderItem.setPrice(cartItem.getCoupPrice());
            orderItem.setName(cartItem.getName());
            orderItem.setNum(cartItem.getNum());

            orderItem.setGoods_id(cartItem.getGoods_id());
            orderItem.setShip_num(0);
            orderItem.setProduct_id(cartItem.getProduct_id());
            orderItem.setOrder_id(order_id);
            orderItem.setGainedpoint(cartItem.getPoint() == null ? 0 : cartItem.getPoint());
            orderItem.setAddon(cartItem.getAddon());

            //3.0新增的三个字段
            orderItem.setSn(cartItem.getSn());
            orderItem.setImage(cartItem.getImage_default());
            orderItem.setCat_id(cartItem.getCatid());

            orderItem.setUnit(cartItem.getUnit());
            
            //分摊 - 满减 跟店铺没关系
//            JSONObject item = discountItemMap.get(cartItem.getId().intValue());
//            if (null != item && item.containsKey("proportion") && item.getDouble("proportion") > 0d) {
//                //计算 在DiscountItem 里面
//                orderItem.setPromotion_proportion(item.getDouble("proportion"));
//            }
            DiscountItem item = discountItemMap.get(cartItem.getId().intValue());
            if (null != item && item.getProportion() > 0d) {
                //计算 在DiscountItem 里面
                orderItem.setPromotion_proportion(item.getProportion());
            }

            //单价 与 店内购买商品总价 的占比
            double itemPriceProportion = cartItem.getPrice() * cartItem.getNum() / storeTotalPrice;

            //分摊 - 运费 真麻烦
            Double shipping = storeShippingMap.get(cartItem.getStore_id().intValue());
            if (null != shipping && shipping > 0d) {
                //商品单价 / 店内购买总价(不算促销等活动) * 店内运费
                //最后一个商品特殊处理
                if (itemList.size() - 1 == i) {
                    if (shipping < otherShippingPrice)
                        shipping = otherShippingPrice;
                    orderItem.setFreight_proportion(shipping - otherShippingPrice);
                } else  {
                    //四舍五入 到1位小数
                    orderItem.setFreight_proportion(Math.round(itemPriceProportion * shipping * 10) / 10d);
                    otherShippingPrice = orderItem.getFreight_proportion() + otherShippingPrice;
                }
            }


            Map<?, ?> activityGoodsMap = activityGoodsManager.getActivityByGoods(cartItem.getGoods_id());
            Integer activityId = activityGoodsMap == null ? null : Integer.valueOf(activityGoodsMap.get("activity_id").toString());
            //没有参加活动的进行优惠劵分摊
            if (activityId == null) {
                //判断该店铺是否存在可以使用优惠劵
                if (storeUseCouponPrice != null && storeUseCouponPrice.containsKey(cartItem.getStore_id())) {
                    //分摊 - 优惠券
                    Double coupon = storeCouponMap.get(cartItem.getStore_id().intValue());
                    if (null != coupon && coupon > 0d) {
                        //商品单价 / 店内购买总价(不算促销等活动) * 店内优惠券值
                        //最后一个商品特殊处理
                        //改店铺可以使用优惠劵商品数量
                        int storeUseCouponLength = storeUseCouponConut.get(cartItem.getStore_id());
                        //判断
                        if (storeUseCouponLength == 1) {
                            if (coupon < otherCouponPrice)
                                coupon = otherCouponPrice;
                            orderItem.setCoupon_proportion(coupon - otherCouponPrice);
                        } else {
                            //四舍五入 到1位小数
                            orderItem.setCoupon_proportion(Math.round(cartItem.getPrice() * cartItem.getNum() / storeUseCouponPrice.get(cartItem.getStore_id()) * coupon * 10) / 10d);
                            otherCouponPrice += orderItem.getCoupon_proportion();

                            storeUseCouponConut.put(cartItem.getStore_id(), storeUseCouponLength - 1);

                        }
                    }
                }
            }

            //分摊 - 红包
            if (null != order.getOrderprice().getBonusPay() && order.getOrderprice().getBonusPay() > 0d) {
                //商品单价 / 购买物品总价(不算促销等活动) * 红包
                //四舍五入 到1位小数
                if (i != itemList.size() - 1) {
                    orderItem.setRed_packets_proportion(Math.round((cartItem.getPrice() * cartItem.getNum() + 
                            orderItem.getFreight_proportion() - orderItem.getCoupon_proportion() - orderItem.getPromotion_proportion())
                            / order.getOrder_amount() * order.getBonus_pay() * 10) / 10d);
                    otherRedPacketsProportion += orderItem.getRed_packets_proportion();
                } else {
                    orderItem.setRed_packets_proportion(order.getBonus_pay() - otherRedPacketsProportion);
                }
            }
            
            //佣金
            Double commission = 0d;
            switch (cartItem.getGoodsCatId()) {
                //葡萄酒
                case 13:
                    commission = s.getWine_commission();
                    break;
                //白酒
                case 85:
                    commission = s.getChinese_spirits_commission();
                    break;
                //洋酒
                case 144:
                    commission = s.getForeign_spirits_commission();
                    break;
                //啤酒
                case 202:
                    commission = s.getBeer_commission();
                    break;
                //黄酒养生酒
                case 255:
                    commission = s.getOther_spirits_commission();
                    break;
                //酒周边
                case 293:
                    commission = s.getAround_commission();
                    break;
            }
            if (null == commission)
                commission = 0d;
            //除以100
            else
                commission = commission / 100;
            orderItem.setCommission_rate(commission);
            Double commissionPrice = cartItem.getPrice() * cartItem.getNum() - orderItem.getPromotion_proportion() - orderItem.getCoupon_proportion();
            orderItem.setCommission(Math.round(commissionPrice * commission * 10) / 10d);
            
            childOrderCommission += orderItem.getCommission();

            this.baseDaoSupport.insert("order_items", orderItem);
            int itemid = this.baseDaoSupport.getLastId("order_items");
            orderItem.setItem_id(itemid);
            orderItemList.add(orderItem);
            this.orderPluginBundle.onItemSave(order, orderItem);
        }

        String itemsJson = JSONArray.fromObject(orderItemList).toString();
        this.daoSupport.execute("update es_order set items_json=?, commission = ? where order_id=?", itemsJson, childOrderCommission, order_id);

    }
    
    /**
     * 保存赠品项
     *
     * @param itemList
     * @param orderid
     * @throws IllegalStateException 会员尚未登录,不能兑换赠品!
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void saveGiftItem(List<CartItem> itemList, Integer orderid) {
        Member member = UserConext.getCurrentMember();
        if (member == null) {
            throw new IllegalStateException("会员尚未登录,不能兑换赠品!");
        }

        int point = 0;
        for (CartItem item : itemList) {
            point += item.getSubtotal().intValue();
            this.baseDaoSupport
                    .execute(
                            "insert into order_gift(order_id,gift_id,gift_name,point,num,shipnum,getmethod)values(?,?,?,?,?,?,?)",
                            orderid, item.getProduct_id(), item.getName(),
                            item.getPoint(), item.getNum(), 0, "exchange");
        }
        if (member.getPoint().intValue() < point) {
            throw new IllegalStateException("会员积分不足,不能兑换赠品!");
        }
        member.setPoint(member.getPoint() - point); // 更新session中的会员积分
        this.baseDaoSupport.execute(
                "update member set point=? where member_id=? ",
                member.getPoint(), member.getMember_id());

    }

    public Page listbyshipid(int pageNo, int pageSize, int status,
                             int shipping_id, String sort, String order) {
        order = " ORDER BY " + sort + " " + order;
        String sql = "select * from order where disabled=0 and status="
                + status + " and shipping_id= " + shipping_id;
        sql += " order by " + order;
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,
                Order.class);
        return page;
    }

    public Page listConfirmPay(int pageNo, int pageSize, String sort, String order) {
        order = " order_id";
        String sql = "select * from order where disabled=0 and ((status = "
                + OrderStatus.ORDER_SHIP + " and payment_type = 'cod') or status= "
                + OrderStatus.ORDER_PAY + "  )";
        sql += " order by " + order;
        //System.out.println(sql);
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, Order.class);
        return page;
    }

    public Order get(Integer orderId) {
        String sql = "select * from order where order_id=?";
        Order order = (Order) this.baseDaoSupport.queryForObject(sql,
                Order.class, orderId);
        return order;
    }


    public Order get(String ordersn) {
        String sql = "select * from es_order where sn='" + ordersn + "'";
        Order order = (Order) this.baseDaoSupport.queryForObject(sql, Order.class);
        return order;

    }

    public List<OrderItem> listGoodsItems(Integer orderId) {

        String sql = "select * from " + this.getTableName("order_items");
        sql += " where order_id = ?";

        List<OrderItem> itemList = this.daoSupport.queryForList(sql, OrderItem.class, orderId);
        this.orderPluginBundle.onFilter(orderId, itemList);
        return itemList;
    }

    @Override
    public List listGoodsItemsByItemId(Integer itemId) {

        String sql = "select i.*,g.is_pack from " + this.getTableName("order_items");
        sql += " i LEFT JOIN es_goods g on i.goods_id = g.goods_id where i.item_id = ?";
        List itemList = this.daoSupport.queryForList(sql, itemId);
        return itemList;
    }

    //获取申请退货订单商品列表
    public List getOrderItem(int order_id) {
        String sql = "select o.*,g.name,g.is_pack from es_goods g INNER JOIN es_order_items o on o.goods_id=g.goods_id where o.order_id = ? ";
        List<Map> items = this.baseDaoSupport.queryForList(sql, order_id);
        for (Map item : items) {
            Object obj = item.get("addon");
            if (obj == null) {
                obj = "";
            }
            String addon = obj.toString();
            if (!StringUtil.isEmpty(addon)) {

                List<Map<String, Object>> specList = JsonUtil.toList(addon);

                FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
                freeMarkerPaser.setClz(this.getClass());
                freeMarkerPaser.putData("specList", specList);
                freeMarkerPaser.setPageName("order_item_spec");
                String html = freeMarkerPaser.proessPageContent();

                item.put("other", html);
            }
        }
        return items;
    }


    public List listGiftItems(Integer orderId) {
        String sql = "select * from order_gift where order_id=?";
        return this.baseDaoSupport.queryForList(sql, orderId);
    }

    /**
     * 读取订单日志
     */

    public List listLogs(Integer orderId) {
        String sql = "select * from order_log where order_id=?";
        return this.baseDaoSupport.queryForList(sql, orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void clean(Integer[] orderId) {
        String ids = StringUtil.arrayToString(orderId, ",");
        String sql = "delete from order where order_id in (" + ids + ")";
        this.baseDaoSupport.execute(sql);

        sql = "delete from order_items where order_id in (" + ids + ")";
        this.baseDaoSupport.execute(sql);

        sql = "delete from order_log where order_id in (" + ids + ")";
        this.baseDaoSupport.execute(sql);

        sql = "delete from payment_logs where order_id in (" + ids + ")";
        this.baseDaoSupport.execute(sql);

        sql = "delete from " + this.getTableName("delivery_item")
                + " where delivery_id in (select delivery_id from "
                + this.getTableName("delivery") + " where order_id in (" + ids
                + "))";
        this.daoSupport.execute(sql);

        sql = "delete from delivery where order_id in (" + ids + ")";
        this.baseDaoSupport.execute(sql);

        orderAllocationManager.clean(orderId);


        /**
         * -------------------
         * 激发订单的删除事件
         * -------------------
         */
        this.orderPluginBundle.onDelete(orderId);


    }

    private boolean exec(Integer[] orderId, int disabled) {
        if (cheack(orderId)) {
            String ids = StringUtil.arrayToString(orderId, ",");
            String sql = "update order set disabled = ? where order_id in (" + ids
                    + ")";
            this.baseDaoSupport.execute(sql, disabled);
            return true;
        } else {
            return false;
        }
    }

    private boolean cheack(Integer[] orderId) {
        boolean i = true;
        for (int j = 0; j < orderId.length; j++) {
            if (this.baseDaoSupport.queryForInt("select status from es_order where order_id=?", orderId[j]) != OrderStatus.ORDER_CANCELLATION) {
                i = false;
            }
        }
        return i;
    }

    public boolean delete(Integer[] orderId) {
        return exec(orderId, 1);

    }

    public void revert(Integer[] orderId) {
        exec(orderId, 0);

    }
    
    /**
     * 获取订单号(日期+三位随机数).
     *
     * @author baoxiufeng
     * @return 订单号
     */
    public String createSn() {
        String sn = null;
        while (this.baseDaoSupport.queryForInt("SELECT COUNT(order_id) FROM es_order WHERE sn=?", sn = OrderUtils.getOrderSn()) > 0) {
        }
        return sn;
    }

//    /**
//     * 创建订单号（日期+两位随机数）
//     */
//    public String createSn() {
//        boolean isHave = true;  //数据库中是否存在该订单
//        String sn = "";            //订单号
//
//        //如果存在当前订单
//        while (isHave) {
//            StringBuffer snSb = new StringBuffer(DateUtil.getDateline() + "");
//            snSb.append(getRandom());
//            String sql = "SELECT count(order_id) FROM es_order WHERE sn = '" + snSb.toString() + "'";
//            int count = this.baseDaoSupport.queryForInt(sql);
//            if (count == 0) {
//                sn = snSb.toString();
//                isHave = false;
//            }
//        }
//
//
//        return sn;
//    }

    /**
     * 获取随机数
     *
     * @return
     */
    public int getRandom() {
        Random random = new Random();
        int num = Math.abs(random.nextInt()) % 100;
        if (num < 10) {
            num = getRandom();
        }
        return num;
    }

    public ICartManager getCartManager() {
        return cartManager;
    }

    public void setCartManager(ICartManager cartManager) {
        this.cartManager = cartManager;
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

    public List listOrderByMemberId(long member_id) {
        String sql = "select * from order where member_id = ? order by create_time desc";
        return this.baseDaoSupport.queryForList(sql, Order.class, member_id);
    }

    public Map mapOrderByMemberId(long memberId) {
        Integer buyTimes = this.baseDaoSupport.queryForInt(
                "select count(0) from order where member_id = ?", memberId);
        Double buyAmount = (Double) this.baseDaoSupport.queryForObject(
                "select sum(paymoney) from order where member_id = ?",
                new DoubleMapper(), memberId);
        Map map = new HashMap();
        map.put("buyTimes", buyTimes);
        map.put("buyAmount", buyAmount);
        return map;
    }

    public IPromotionManager getPromotionManager() {
        return promotionManager;
    }

    public void setPromotionManager(IPromotionManager promotionManager) {
        this.promotionManager = promotionManager;
    }

    public void edit(Order order) {
        this.baseDaoSupport.update("order", order,
                "order_id = " + order.getOrder_id());

    }

    public List<Map> listAdjItem(Integer orderid) {
        String sql = "select * from order_items where order_id=? and addon!=''";
        return this.baseDaoSupport.queryForList(sql, orderid);
    }


    /**
     * 统计订单状态
     */
    public Map censusState() {

        // 构造一个返回值Map，并将其初始化：各种订单状态的值皆为0
        Map<String, Integer> stateMap = new HashMap<String, Integer>(7);
        String[] states = {"cancel_ship", "cancel_pay", "pay", "ship", "complete", "allocation_yes"};
        for (String s : states) {
            stateMap.put(s, 0);
        }

        // 分组查询、统计订单状态
        String sql = "select count(0) num,status  from "
                + this.getTableName("order")
                + " where disabled = 0 group by status";
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
//		// 将list转为map
        for (Map<String, Integer> state : list) {
            stateMap.put(this.getStateString(state.get("status")), state.get("num"));
        }

        sql = "select count(0) num  from " + this.getTableName("order") + " where disabled = 0  and status=0 ";
        int count = this.daoSupport.queryForInt(sql);
        stateMap.put("wait", 0);

        sql = "select count(0) num  from " + this.getTableName("order") + " where disabled = 0  ";
        sql += " and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ") ";//非货到付款的，未付款状态的可以结算
        sql += " or ( payment_id=8 and status!=" + OrderStatus.ORDER_NOT_PAY + "  and  pay_status!=" + OrderStatus.PAY_CONFIRM + ")";
        sql += " or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status=" + OrderStatus.ORDER_ROG + " )  ) )";//货到付款的要发货或收货后才能结算
        count = this.daoSupport.queryForInt(sql);
        stateMap.put("not_pay", count);

        sql = "select count(0) from es_order where disabled=0  and ( ( payment_type!='cod' and payment_id!=8  and  status=2)  or ( payment_type='cod' and  status=0))";
        count = this.baseDaoSupport.queryForInt(sql);
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


    public OrderPluginBundle getOrderPluginBundle() {
        return orderPluginBundle;
    }

    public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
        this.orderPluginBundle = orderPluginBundle;
    }

    @Override
    public String export(Date start, Date end) {
        String sql = "select * from order where disabled=0 ";
        if (start != null) {
            sql += " and create_time>" + start.getTime();
        }

        if (end != null) {
            sql += "  and create_timecreate_time<" + end.getTime();
        }

        List<Order> orderList = this.baseDaoSupport.queryForList(sql, Order.class);


        //使用excel导出流量报表
        ExcelUtil excelUtil = new ExcelUtil();

        //流量报表excel模板在类包中，转为流给excelutil
        InputStream in = FileUtil.getResourceAsStream("com/enation/app/shop/core/service/impl/order.xls");

        excelUtil.openModal(in);
        int i = 1;
        for (Order order : orderList) {

            excelUtil.writeStringToCell(i, 0, order.getSn()); //订单号
            excelUtil.writeStringToCell(i, 1, DateUtil.toString(new Date(order.getCreate_time()), "yyyy-MM-dd HH:mm:ss")); //下单时间
            excelUtil.writeStringToCell(i, 2, order.getOrderStatus()); //订单状态
            excelUtil.writeStringToCell(i, 3, "" + order.getOrder_amount()); //订单总额
            excelUtil.writeStringToCell(i, 4, order.getShip_name()); //收货人
            excelUtil.writeStringToCell(i, 5, order.getPayStatus()); //付款状态
            excelUtil.writeStringToCell(i, 6, order.getShipStatus()); //发货状态
            excelUtil.writeStringToCell(i, 7, order.getShipping_type()); //配送方式
            excelUtil.writeStringToCell(i, 8, order.getPayment_name()); //支付方式
            i++;
        }
        //String target= EopSetting.IMG_SERVER_PATH;
        //saas 版导出目录用户上下文目录access文件夹
        String filename = "/order";
        String static_server_path = SystemSetting.getStatic_server_path();
        File file = new File(static_server_path + filename);
        if (!file.exists()) file.mkdirs();

        filename = filename + "/order" + com.enation.framework.util.DateUtil.getDateline() + ".xls";
        excelUtil.writeToFile(static_server_path + filename);
        String static_server_domain = SystemSetting.getStatic_server_domain();

        return static_server_domain + filename;
    }


    @Override
    public OrderItem getItem(int itemid) {

        String sql = "select items.*,p.store as store from "
                + this.getTableName("order_items") + " items ";
        sql += " left join " + this.getTableName("product")
                + " p on p.product_id = items.product_id ";
        sql += " where items.item_id = ?";

        OrderItem item = (OrderItem) this.daoSupport.queryForObject(sql,
                OrderItem.class, itemid);

        return item;
    }


    public IAdminUserManager getAdminUserManager() {
        return adminUserManager;
    }

    public void setAdminUserManager(IAdminUserManager adminUserManager) {
        this.adminUserManager = adminUserManager;
    }

    public IPermissionManager getPermissionManager() {
        return permissionManager;
    }

    public void setPermissionManager(IPermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public IRoleManager getRoleManager() {
        return roleManager;
    }

    public void setRoleManager(IRoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public ErpManager getErpManager() {
        return erpManager;
    }

    public void setErpManager(ErpManager erpManager) {
        this.erpManager = erpManager;
    }

    /**
     *
     */
    public int getMemberOrderNum(long member_id, int payStatus) {
        return this.baseDaoSupport
                .queryForInt(
                        "SELECT COUNT(0) FROM order WHERE member_id=? AND pay_status=?",
                        member_id, payStatus);
    }

    /**
     * by dable
     */
    @Override
    public List<Map> getItemsByOrderid(Integer order_id) {
        String sql = "select * from order_items where order_id=?";
        return this.baseDaoSupport.queryForList(sql, order_id);
    }

    @Override
    public void refuseReturn(String orderSn) {
        this.baseDaoSupport.execute("update order set state = -5 where sn = ?",
                orderSn);
    }

    /**
     * 更新订单价格
     */
    @Override
    public void updateOrderPrice(double price, int orderid) {
        this.baseDaoSupport
                .execute(
                        "update order set order_amount = order_amount-?,goods_amount = goods_amount- ? where order_id = ?",
                        price, price, orderid);
    }

    /**
     * 根据id查询物流公司
     */
    @Override
    public String queryLogiNameById(Integer logi_id) {
        return (String) this.baseDaoSupport.queryForObject(
                "select name from logi_company where id=?", new StringMapper(),
                logi_id);
    }

    /**
     * 游客订单查询
     */
    public Page searchForGuest(int pageNo, int pageSize, String ship_name,
                               String ship_tel) {
        String sql = "select * from order where ship_name=? AND (ship_mobile=? OR ship_tel=?) and member_id is null ORDER BY order_id DESC";
        Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, Order.class, ship_name, ship_tel, ship_tel);
        return page;
    }

    public Page listByStatus(int pageNo, int pageSize, int status, long memberid) {
        String filedname = "status";
        if (status == 0) {
            // 等待付款的订单 按付款状态查询
            filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
                    + " AND pay_status";
        }
        String sql = "select * from order where " + filedname
                + "=? AND member_id=? ORDER BY order_id DESC";
        Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,
                pageSize, Order.class, status, memberid);
        return page;
    }

    public Page listByStatus(int pageNo, int pageSize, long memberid) {
        String sql = "select e.sn,e.status,e.pay_status,e.create_time from es_order e where member_id=? and parent_id>0 ORDER BY order_id DESC";
        Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,
                pageSize, Order.class, memberid);
        return page;
    }

    public List<Order> listByStatus(int status, long memberid) {
        String filedname = "status";
        if (status == 0) {
            // 等待付款的订单 按付款状态查询
            filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
                    + " AND pay_status";
        }
        String sql = "select * from order where " + filedname
                + "=? AND member_id=? ORDER BY order_id DESC";

        return this.baseDaoSupport.queryForList(sql, status, memberid);

    }

    public int getMemberOrderNum(long member_id) {
        return this.baseDaoSupport.queryForInt(
                "SELECT COUNT(0) FROM order WHERE member_id=?", member_id);
    }

    @Override
    public Page search(int pageNO, int pageSize, int disabled, String sn,
                       String logi_no, String uname, String ship_name, int status, Integer paystatus) {

        StringBuffer sql = new StringBuffer(
                "select * from " + this.getTableName("order") + " where disabled=?  ");
        if (status != -100) {
            if (status == -99) {
                /*
				 * 查询未处理订单
				 * */
                sql.append(" and ((payment_type='cod' and status=0 )  or (payment_type!='cod' and status=1 )) ");
            } else
                sql.append(" and status = " + status + " ");

        }
        if (paystatus != null && paystatus != -100) {
            sql.append(" and pay_status = " + paystatus + " ");
        }

        if (!StringUtil.isEmpty(sn)) {
            sql.append(" and sn = '" + sn + "' ");
        }
        if (!StringUtil.isEmpty(uname)) {
            sql.append(" and member_id  in ( SELECT  member_id FROM " + this.getTableName("member") + " where uname = '"
                    + uname + "' )  ");
        }
        if (!StringUtil.isEmpty(ship_name)) {
            sql.append(" and  ship_name = '" + ship_name + "' ");
        }
        if (!StringUtil.isEmpty(logi_no)) {
            sql.append(" and order_id in (SELECT order_id FROM " + this.getTableName("delivery") + " where logi_no = '"
                    + logi_no + "') ");
        }
        sql.append(" order by create_time desc ");
        Page page = this.daoSupport.queryForPage(sql.toString(), pageNO,
                pageSize, Order.class, disabled);
        return page;

    }

    @Override
    public Page search(int pageNO, int pageSize, int disabled, String sn,
                       String logi_no, String uname, String ship_name, int status) {
        return search(pageNO, pageSize, disabled, sn,
                logi_no, uname, ship_name, status, null);
    }

    public Order getNext(String next, Integer orderId, Integer status,
                         int disabled, String sn, String logi_no, String uname,
                         String ship_name) {
        StringBuffer sql = new StringBuffer(
                "select * from " + this.getTableName("order") + " where  1=1  ");

        StringBuffer depotsql = new StringBuffer("  ");
        AdminUser adminUser = UserConext.getCurrentAdminUser();
        if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
            boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin")); //检测是否是发货员


            boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
            if (isShiper && !haveOrder) {
                DepotUser depotUser = (DepotUser) adminUser;
                int depotid = depotUser.getDepotid();
                depotsql.append(" and depotid=" + depotid + "  ");
            }
        }

        StringBuilder sbsql = new StringBuilder("  ");
        if (status != null && status != -100) {
            sbsql.append(" and status = " + status + " ");
        }
//		if (!StringUtil.isEmpty(sn)) {
//			sbsql.append(" and sn = '" + sn.trim() + "' ");
//		}
        if (!StringUtil.isEmpty(uname) && !uname.equals("undefined")) {
            sbsql.append(" and member_id  in ( SELECT  member_id FROM " + this.getTableName("member") + " where uname = '"
                    + uname + "' )  ");
        }
        if (!StringUtil.isEmpty(ship_name)) {
            sbsql.append("  and  ship_name = '" + ship_name.trim() + "'  ");
        }
        if (!StringUtil.isEmpty(logi_no) && !logi_no.equals("undefined")) {
            sbsql.append("  and order_id in (SELECT order_id FROM " + this.getTableName("delivery") + " where logi_no = '" + logi_no + "')  ");
        }
        if (next.equals("previous")) {
            sql.append("  and order_id IN (SELECT CASE WHEN SIGN(order_id - "
                    + orderId
                    + ") < 0 THEN MAX(order_id)  END AS order_id FROM " + this.getTableName("order") + " WHERE order_id <> "
                    + orderId + depotsql.toString() + " and disabled=? " + sbsql.toString() + " GROUP BY SIGN(order_id - " + orderId
                    + ") ORDER BY SIGN(order_id - " + orderId + "))   ");
            //MAX 及SIGN 函数经试验均可在mysql及oracle中通过，但mssql未验证
        } else if (next.equals("next")) {
            sql.append("  and  order_id in (SELECT CASE WHEN SIGN(order_id - "
                    + orderId
                    + ") > 0 THEN MIN(order_id) END AS order_id FROM " + this.getTableName("order") + " WHERE order_id <> "
                    + orderId + depotsql.toString() + " and disabled=? " + sbsql.toString() + " GROUP BY SIGN(order_id - " + orderId
                    + ") ORDER BY SIGN(order_id - " + orderId + "))   ");
        } else {
            return null;
        }
        sql.append(" order by create_time desc ");
        ////System.out.println(sql);
        Order order = (Order) this.daoSupport.queryForObject(sql.toString(),
                Order.class, disabled);
        return order;
    }

    /**
     * 获取订单中商品的总价格
     *
     * @param sessionid
     * @return
     */
    @SuppressWarnings("unused")
	private double getOrderTotal(String sessionid) {
        List goodsItemList = cartManager.listGoods(sessionid);
        double orderTotal = 0d;
        if (goodsItemList != null && goodsItemList.size() > 0) {
            for (int i = 0; i < goodsItemList.size(); i++) {
                CartItem cartItem = (CartItem) goodsItemList.get(i);
                orderTotal += cartItem.getCoupPrice() * cartItem.getNum();
            }
        }
        return orderTotal;
    }


    private OrderItem getOrderItem(Integer itemid) {
        return (OrderItem) this.baseDaoSupport.queryForObject("select * from order_items where item_id = ?", OrderItem.class, itemid);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delItem(Integer itemid, Integer itemnum) {//删除订单货物
        OrderItem item = this.getOrderItem(itemid);
        Order order = this.get(item.getOrder_id());
        boolean flag = false;
        int paymentid = order.getPayment_id();
        int status = order.getStatus();
        if ((paymentid == 1 || paymentid == 3 || paymentid == 4 || paymentid == 5) && (status == 0 || status == 1 || status == 2 || status == 3 || status == 4)) {
            flag = true;
        }
        if ((paymentid == 2) && (status == 0 || status == 9 || status == 3 || status == 4)) {
            flag = true;
        }
        if (flag) {
            try {
                if (itemnum.intValue() <= item.getNum().intValue()) {
                    Goods goods = goodsManager.getGoods(item.getGoods_id());
                    double order_amount = order.getOrder_amount();
                    double itemprice = item.getPrice().doubleValue() * itemnum.intValue();
                    double leftprice = CurrencyUtil.sub(order_amount, itemprice);
                    int difpoint = (int) Math.floor(leftprice);
                    Double[] dlyprice = this.dlyTypeManager.countPrice(order.getShipping_id(), order.getWeight() - (goods.getWeight().doubleValue() * itemnum.intValue()), leftprice, order.getShip_regionid().toString());
                    double sumdlyprice = dlyprice[0];
                    this.baseDaoSupport.execute("update order set goods_amount = goods_amount- ?,shipping_amount = ?,order_amount =  ?,weight =  weight - ?,gainedpoint =  ? where order_id = ?"
                            , itemprice, sumdlyprice, leftprice, (goods.getWeight().doubleValue() * itemnum.intValue()), difpoint, order.getOrder_id());
                    this.baseDaoSupport.execute("update freeze_point set mp =?,point =?  where orderid = ? and type = ?", difpoint, difpoint, order.getOrder_id(), "buygoods");
                    if (itemnum.intValue() == item.getNum().intValue()) {
                        this.baseDaoSupport.execute("delete from order_items where item_id = ?", itemid);
                    } else {
                        this.baseDaoSupport.execute("update order_items set num = num - ? where item_id = ?", itemnum.intValue(), itemid);
                    }

                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        return flag;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public boolean saveAddrDetail(String addr, int orderid) {
        if (addr == null || StringUtil.isEmpty(addr)) {
            return false;
        } else {
            this.baseDaoSupport.execute("update order set ship_addr=?  where order_id=?", addr, orderid);
            return true;
        }
    }

    @Override
    public boolean saveShipInfo(String remark, String ship_day, String ship_name,
                                String ship_tel, String ship_mobile, String ship_zip, int orderid) {
        try {
            if (ship_day != null && !StringUtil.isEmpty(ship_day)) {
                this.baseDaoSupport.execute("update order set ship_day=?  where order_id=?", ship_day, orderid);
                if (remark != null && !StringUtil.isEmpty(remark) && !remark.equals("undefined")) {
                    StringBuilder sb = new StringBuilder("");
                    sb.append("【配送时间：");
                    sb.append(remark.trim());
                    sb.append("】");
                    this.baseDaoSupport.execute("update order set remark= concat(remark,'" + sb.toString() + "')   where order_id=?", orderid);
                }
                return true;
            }
            if (ship_name != null && !StringUtil.isEmpty(ship_name)) {
                this.baseDaoSupport.execute("update order set ship_name=?  where order_id=?", ship_name, orderid);
                return true;
            }
            if (ship_tel != null && !StringUtil.isEmpty(ship_tel)) {
                this.baseDaoSupport.execute("update order set ship_tel=?  where order_id=?", ship_tel, orderid);
                return true;
            }
            if (ship_mobile != null && !StringUtil.isEmpty(ship_mobile)) {
                this.baseDaoSupport.execute("update order set ship_mobile=?  where order_id=?", ship_mobile, orderid);
                return true;
            }
            if (ship_zip != null && !StringUtil.isEmpty(ship_zip)) {
                this.baseDaoSupport.execute("update order set ship_zip=?  where order_id=?", ship_zip, orderid);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void updatePayMethod(int orderid, int payid, String paytype, String payname) {

        this.baseDaoSupport.execute("update order set payment_id=?,payment_type=?,payment_name=? where order_id=?", payid, paytype, payname, orderid);

    }

    /*
     * (non-Javadoc)
     * @see com.enation.javashop.core.service.IOrderManager#checkProInOrder(int)
     */
    @Override
    public boolean checkProInOrder(int productid) {
        String sql = "select count(0) from order_items where product_id=?";
        return this.baseDaoSupport.queryForInt(sql, productid) > 0;
    }

    /*
     * (non-Javadoc)
     * @see com.enation.javashop.core.service.IOrderManager#checkGoodsInOrder(int)
     */
    @Override
    public boolean checkGoodsInOrder(int goodsid) {
        String sql = "select count(0) from order_items where goods_id=?";
        return this.baseDaoSupport.queryForInt(sql, goodsid) > 0;
    }

    @Override
    public List listByOrderIds(Integer[] orderids, String order) {
        try {
            StringBuffer sql = new StringBuffer("select * from es_order where disabled=0 ");

            if (orderids != null && orderids.length > 0)
                sql.append(" and order_id in (" + StringUtil.arrayToString(orderids, ",") + ")");

            if (StringUtil.isEmpty(order)) {
                order = "create_time desc";
            }
            sql.append(" order by  " + order);
            return this.daoSupport.queryForList(sql.toString(), Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Page list(int pageNO, int pageSize, int disabled, String order) {

        StringBuffer sql = new StringBuffer("select * from order where disabled=? ");

        AdminUser adminUser = UserConext.getCurrentAdminUser();
        if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
            boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_ship")); //检测是否是发货员
            boolean haveAllo = this.permissionManager
                    .checkHaveAuth(PermissionConfig.getAuthId("allocation")); // 配货下达权限
            boolean haveOrder = this.permissionManager
                    .checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
            if (isShiper && !haveAllo && !haveOrder) {
                DepotUser depotUser = (DepotUser) adminUser;
                int depotid = depotUser.getDepotid();
                sql.append(" and depotid=" + depotid);
            }
        }

        order = StringUtil.isEmpty(order) ? "order_id desc" : order;
        sql.append(" order by " + order);
        return this.baseDaoSupport.queryForPage(sql.toString(), pageNO, pageSize, Order.class, disabled);
    }

    public Integer orderStatusNum(Integer status) {
        Member member = UserConext.getCurrentMember();
        String sql = "select count(0) from es_order where status =? and member_id=?";
        return this.baseDaoSupport.queryForInt(sql, status, member.getMember_id());
    }


    public Page list(int pageNo, int pageSize, int status, int depotid,
                     String order) {
        order = StringUtil.isEmpty(order) ? "order_id desc" : order;
        String sql = "select * from order where disabled=0 and status="
                + status;
        if (depotid > 0) {
            sql += " and depotid=" + depotid;
        }
        sql += " order by " + order;
        Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, Order.class);
        return page;
    }

    @Override
    public Page listOrder(Map map, int page, int pageSize, String other, String order) {

        String sql = createTempSql(map, other, order);
        Page webPage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
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
        Integer depotid = (Integer) map.get("depotid");
        String complete = (String) map.get("complete");

        StringBuffer sql = new StringBuffer();
        sql.append("select * from order where disabled=0 ");

        if (stype != null && keyword != null) {
            if (stype == 0) {
                sql.append(" and (sn like '%" + keyword + "%'");
                sql.append(" or ship_name like '%" + keyword + "%')");
            }
        }

        if (status != null) {
            sql.append("and status=" + status);
        }

        if (sn != null && !StringUtil.isEmpty(sn)) {
            sql.append(" and sn like '%" + sn + "%'");
        }

        if (ship_name != null && !StringUtil.isEmpty(ship_name)) {
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

        if (depotid != null && depotid > 0) {
            sql.append(" and depotid=" + depotid);
        }

        if (start_time != null && !StringUtil.isEmpty(start_time)) {
            long stime = com.enation.framework.util.DateUtil.getDateline(start_time + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            sql.append(" and create_time>" + stime);
        }
        if (end_time != null && !StringUtil.isEmpty(end_time)) {
            long etime = com.enation.framework.util.DateUtil.getDateline(end_time + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            sql.append(" and create_time<" + etime);
        }
        if (!StringUtil.isEmpty(orderstate)) {
            if (orderstate.equals("wait_ship")) { //对待发货的处理
                sql.append(" and ( ( payment_type!='cod' and payment_id!=8  and  status=" + OrderStatus.ORDER_PAY_CONFIRM + ") ");//非货到付款的，要已结算才能发货
                sql.append(" or ( payment_type='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ")) ");//货到付款的，新订单（已确认的）就可以发货
            } else if (orderstate.equals("wait_pay")) {
                sql.append(" and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ") ");//非货到付款的，未付款状态的可以结算
                sql.append(" or ( payment_id=8 and status!=" + OrderStatus.ORDER_NOT_PAY + "  and  pay_status!=" + OrderStatus.PAY_CONFIRM + ")");
                sql.append(" or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status=" + OrderStatus.ORDER_ROG + " )  ) )");//货到付款的要发货或收货后才能结算

            } else if (orderstate.equals("wait_rog")) {
                sql.append(" and status=" + OrderStatus.ORDER_SHIP);
            } else {
                sql.append(" and status=" + orderstate);
            }

        }

        if (!StringUtil.isEmpty(complete)) {
            sql.append(" and status=" + OrderStatus.ORDER_COMPLETE);
        }

        sql.append(" ORDER BY " + other + " " + order);

        System.out.println(sql.toString());
        return sql.toString();
    }

    public void saveDepot(int orderid, int depotid) {
        this.orderPluginBundle.onOrderChangeDepot(this.get(orderid), depotid, this.listGoodsItems(orderid));
        this.daoSupport.execute("update es_order set depotid=?  where order_id=?", depotid, orderid);
    }

    public void savePayType(int orderid, int paytypeid) {
        PayCfg cfg = this.paymentManager.get(paytypeid);
        String typename = cfg.getName();
        String paytype = cfg.getType();
        this.daoSupport.execute("update es_order set payment_id=?,payment_name=?,payment_type=? where order_id=?", paytypeid, typename, paytype, orderid);
    }

    public void saveShipType(int orderid, int shiptypeid) {
        String typename = this.dlyTypeManager.getDlyTypeById(shiptypeid).getName();
        this.daoSupport.execute("update es_order set shipping_id=?,shipping_type=? where order_id=?", shiptypeid, typename, orderid);
    }

    @Override
    public void add(Order order) {
        this.baseDaoSupport.insert("es_order", order);
    }

    @Override
    public void saveAddr(int orderId, int ship_provinceid, int ship_cityid, int ship_regionid, String Attr) {
        this.daoSupport.execute("update es_order set ship_provinceid=?,ship_cityid=?,ship_regionid=?,shipping_area=? where order_id=?", ship_provinceid, ship_cityid, ship_regionid, Attr, orderId);
    }

    @Override
    public Integer getOrderGoodsNum(int order_id) {
        String sql = "select count(0) from order_items where order_id =?";
        return this.baseDaoSupport.queryForInt(sql, order_id);
    }

    @Override
    public List getOrderItemDetail(int item_id) {
        String sql = "SELECT c.*,g.mktprice from es_order_item_child c INNER JOIN es_goods g ON g.goods_id=c.goodsid where itemid=? ORDER BY c.goodsid";
        return this.baseDaoSupport.queryForList(sql, item_id);
    }


    @Override
    public boolean getOrderByMemberid(String sn, long memberid) {
        boolean flag = false;
        String sql = "select count(0) from es_order where member_id=? and sn=?";
        Integer num = this.daoSupport.queryForInt(sql, memberid, sn);
        if (num == 1) {
            flag = true;
        }
        return flag;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePaymentTime(Integer orderid) {
        String sql = "update es_order set payment_time = ? where order_id = ? or parent_id=?";
        baseDaoSupport.execute(sql, com.enation.framework.util.DateUtil.getDateline(), orderid, orderid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
	public void updatePaymentInfo(Integer orderid, PayCfg payCfg) {
    	// 重新设置支付方式
    	String sql = "update es_order set payment_id=?, payment_type=?, payment_name=?, payment_time=? where order_id=? or parent_id=?";
    	baseDaoSupport.execute(sql, payCfg.getId(), payCfg.getType(), payCfg.getName(), DateUtil.getDateline(), orderid, orderid);
    	sql = "update es_payment_logs set pay_method=? where order_id in (select order_id from es_order where order_id=? or parent_id=?)";
    	baseDaoSupport.execute(sql, payCfg.getName(), orderid, orderid);
	}

	public IMemberManager getMemberManager() {
        return memberManager;
    }

    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    public CartPluginBundle getCartPluginBundle() {
        return cartPluginBundle;
    }

    public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
        this.cartPluginBundle = cartPluginBundle;
    }

    @Override
    public List<Map<String, Object>> listForErp1(Map<String, Object> map, String sort, String order) {
        String status = (String) map.get("status");
        String paystatus = (String) map.get("paystatus");
        String shipstatus = (String) map.get("shipstatus");
        String payment_id = (String) map.get("payment_id");
        String storeId = (String) map.get("store_id");

        String sql = "";
        sql += "select o.order_id as order_id, o.sn as sn, o.status as status,o.ship_name as ship_name, o.ship_addr as ship_addr, "
                + "o.ship_zip as ship_zip, o.ship_email as ship_email, o.ship_mobile as ship_mobile, o.ship_tel as ship_tel, "
                + "o.ship_day as ship_day, o.ship_time as ship_time, o.is_protect as is_protect, o.protect_price as protect_price, "
                + "o.goods_amount as goods_amount, o.shipping_amount as shipping_amount, o.order_amount as order_amount, "
                + "o.weight as weight, o.goods_num as goods_num, o.depotid as depotid, o.store_id as store_id, "
                + "o.is_storage as is_storage, o.start_storage_time as start_storage_time, o.end_storage_time as end_storage_time, "
                + "o.storage_date as storage_date, r.title as recipt_title , r.add_time as receipt_add_time, r.content as receipt_content, "
                + "o.ship_provinceid as ship_provinceid, o.ship_cityid as ship_cityid, o.ship_regionid as ship_regionid, "
                + "o.discount as discount_pay, o.bonus_pay as bonus_pay, o.advance_pay as advance_pay, o.virtual_pay as virtual_pay,"
                + "o.need_pay_money as need_pay_money, o.payment_name as payment_name, o.pay_status, o.logi_name as shipping_type "
                + "from es_order o left join es_receipt r on o.order_id = r.order_id  where (is_erp_process != 1 or is_erp_process is null) ";
        if (status != null) {
            sql += "and o.status=" + status;
        }

        if (paystatus != null) {
            sql += " and o.pay_status=" + paystatus;
        }

        if (shipstatus != null) {
            sql += " and o.ship_status=" + shipstatus;
        }

        if (payment_id != null) {
            sql += " and o.payment_id=" + payment_id;
        }

        if (storeId != null) {
            sql += " and o.store_id=" + storeId;
        }
        sql += " AND o.parent_id is NOT NULL  ORDER BY " + sort + " " + order;

        //System.out.println(sql);

        return this.baseDaoSupport.queryForList(sql);

    }

    public List<Map<String, Object>> getOrderItemList(int orderId) {
        String sql = "select g.sn, oi.num, oi.price "
                + "from es_order_items oi left join es_goods g on oi.goods_id=g.goods_id where oi.order_id=" + orderId;
        //System.out.println(sql);
        return this.baseDaoSupport.queryForList(sql);
    }

    /*
     * erp状态,已取走, 订单号都是父订单
     *
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateErpFlag(Integer[] orderIdArray) {
        if (orderIdArray == null || orderIdArray.length == 0) {
            throw new RuntimeException("订单号不存在");
        }
        String ids = StringUtil.arrayToString(orderIdArray, ",");
		//更新父订单
        String sql = "update es_order set is_erp_process=1 where (is_erp_process=0 or is_erp_process is null) and (parent_id is null) and order_id in (" + ids + ")";
        baseDaoSupport.execute(sql);
		//更新子订单
		sql = "update es_order set is_erp_process=1 where parent_id in (" + ids + ")";
		baseDaoSupport.execute(sql);
    }

    
    


    /*
     * erp 改变状态
     *
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateErpStatus(int status, Integer[] orderIdArray) {
        
        if (orderIdArray == null) {
            throw new RuntimeException("订单号不存在");
        }


        String ids = StringUtil.arrayToString(orderIdArray, ",");

        String sql = null;
        if (status == OrderStatus.ORDER_ROG) {
            //如果是妥投,要记录妥投时间,给结算使用.不过按理说不会.
            sql = "update es_order set status = ?,signing_time =? where order_id in (" + ids + ")";
            baseDaoSupport.execute(sql, status, DateUtil.getDateline());
        } else {
            int mstatus = 0;
            if(status==2 || status==3){
                mstatus=-2;
            }
            sql = "update es_order set status = ? where order_id in (" + ids + ")";
            baseDaoSupport.execute(sql, mstatus);

            //接口拒收创建退货单
            if(status==3){
                log.info("接口拒收创建退货单:开始");
                try{
                    for (Integer orderId : orderIdArray){
                        log.info("接口拒收创建退货单:循环开始:"+orderId);
                        //创建退货单
                        StoreSellBackList sellBackList=new StoreSellBackList();
                        //订单信息
                        StoreOrder order = storeOrderManager.get(orderId);
                        //记录会员信息
                        Member member =  storeMemberManager.getMember(order.getMember_id());
                        sellBackList.setMember_id(member.getMember_id());
                        sellBackList.setSndto(member.getName());
                        
                        //插入退款单信息
                        sellBackList.setAdr(order.getShip_addr());
                        sellBackList.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));//退货单号
                        sellBackList.setOrderid(orderId);
                        sellBackList.setOrdersn(order.getSn());
                        sellBackList.setRegoperator("会员");
                        sellBackList.setTel(order.getShip_tel());
                        sellBackList.setZip(order.getShip_zip());
                        sellBackList.setTradestatus(0);
                        sellBackList.setRegtime(DateUtil.getDateline());
                        sellBackList.setDepotid(order.getDepotid());
                        sellBackList.setRemark("用户拒收，接口调用");
                        sellBackList.setRefund_way("余额退款");
                        sellBackList.setStore_id(order.getStore_id());
                        sellBackList.setAlltotal_pay(order.getOrder_amount());
                        sellBackList.setReturn_price(order.getOrder_amount()); // 退款金额
                        sellBackList.setTradestatus(3);
                        this.baseDaoSupport.insert("es_sellback_list", sellBackList);
                        int sid = this.baseDaoSupport.getLastId("es_sellback_list");
                        log.info("接口拒收创建退货单:循环结束:"+orderId);
                        log.info("接口拒收创建退货商品单:循环开始:"+orderId);
                        //开始创建退货单商品
                        List<Map> itemlist = this.getItemsByOrderid(orderId);
                        for(int h=0; h<itemlist.size(); h++){
                            SellBackGoodsList data = new SellBackGoodsList();
                            data.setGoods_id(NumberUtils.toInt(itemlist.get(h).get("goods_id").toString()));
                            data.setPrice(NumberUtils.toDouble(itemlist.get(h).get("price").toString()));
                            data.setProduct_id(NumberUtils.toInt(itemlist.get(h).get("product_id").toString()));
                            data.setReturn_num(NumberUtils.toInt(itemlist.get(h).get("num").toString()));
                            data.setShip_num(NumberUtils.toInt(itemlist.get(h).get("num").toString()));
                            data.setRecid(sid);
                            sellBackManager.saveGoodsList(data);
                        }
                        log.info("接口拒收创建退货商品单:循环结束:"+orderId);
                    }
                }catch(Exception e){
                    log.info("接口拒收创建退货单:报异常:"+e.toString());
                }
            }

            // MOSOON 4-13修改不一定对 需要测试一下 保留此todo好查询到
            for (Integer orderId : orderIdArray) {
                Order order = this.get(orderId);
                List<Map<String, Object>> list = order.getItemList();
                for (Map<String, Object> map : list) {
                    int goodsid = NumberUtils.toInt(map.get("goods_id").toString());
                    int num = NumberUtils.toInt(map.get("num").toString());
                    this.daoSupport.execute("update es_product set store=store+?,enable_store=enable_store+?  where goods_id=?",num, num,goodsid);
                    this.daoSupport.execute("update es_goods set store=store+?,enable_store=enable_store+?  where goods_id=?",num, num,goodsid);
                }
            }
        }

    }

    /*
     * update 发货状态
     * 承运商编码／名称：100001/宅急送,100002/其他,100003／EMS    其实实际上只有宅急送
     *
     */
    @Override
    public void updateShipStatus(List<Element> entries) {
        try{
        for (Element entry : entries) {
            String cvouchcode = entry.element("cvouchcode").getTextTrim(); //小订单号  获得订单
            log.info("updateShipStatus:cvouchcode"+cvouchcode);
            //1-销售出库，2-退货入库，3-拒收入库
            //实际上,按照OrderStatus弄好了.
            String voucher_type = entry.element("voucher_type").getTextTrim();
            log.info("updateShipStatus:voucher_type"+voucher_type);
            String carrierCode = entry.element("carrierCode").getTextTrim();//承运商编号
            log.info("updateShipStatus:carrierCode"+carrierCode);
            String shipCode = entry.element("shipCode").getTextTrim();//运单编号
            log.info("updateShipStatus:shipCode"+shipCode);
            //转换编码
            if("100001".equals(carrierCode)){
            	carrierCode = "zhaijisong";
            }
            //voucher_type到底填写什么
            Order order = this.get(cvouchcode);//订单
                if (order != null) {
                    Integer order_id = order.getOrder_id();
                    //先查询es_delivery表是否有信息
                    List list = baseDaoSupport.queryForList("select * from es_delivery where order_id=?", order_id);
                    if(list!=null && list.size()>0){
                        String sql = "update es_delivery set type=?, logi_code=?, logi_no=? where order_id= ?";
                        baseDaoSupport.execute(sql, voucher_type, carrierCode, shipCode, order_id);
                    }else{
                        String sql = "insert into es_delivery (type,order_id,member_id,money,ship_type,logi_id,logi_name,"
                                + "logi_no,ship_name,region_id,city_id,province_id,"
                                + "ship_addr,ship_zip,ship_mobile,create_time,logi_code,is_protect) "
                                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        baseDaoSupport.execute(sql, voucher_type, order_id,order.getMember_id(),order.getShipping_amount(),"宅急送",2,"宅急送",
                                shipCode,order.getShip_name(),order.getShip_regionid(),order.getShip_cityid(),order.getShip_provinceid(),
                                order.getShip_addr(),order.getShip_zip(),order.getShip_mobile(),System.currentTimeMillis(),"zhaijisong",0);
                    }
                    if(order.getStatus()==2){//如果是付款状态，才更新状态未出库
                        String msql = "update es_order set status=5, ship_status=3, sale_cmpl_time=? where order_id= ?";
                        log.info("updateShipStatus:更新数据库状态："+order_id);
                        baseDaoSupport.execute(msql, DateUtil.getDateline(),order_id);
                        log.info("updateShipStatus:更新数据库成功："+order_id);
                    }
                }
            }
        }catch(Exception e){
            log.info("updateShipStatus:e"+e.toString());
        }
    }

    public boolean check(Integer order_id) {
        boolean i = false;
        int result = this.baseDaoSupport.queryForInt("select status from es_order where order_id=?", order_id);
        if (result != 8) {
            i = true;
        }

        return i;
    }

    @Override
    public Page listByStatusForApp(int pageNo, int pageSize, String status, long member_id) {
        String filedname = "status";

        String sql = "";
        Page page = null;
        if (status.equals("0")) {
            // 等待付款的订单 按付款状态查询
            filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
                    + " AND pay_status";
            sql = "select * from order where " + filedname
                    + "=? AND member_id=? AND parent_id is NOT NULL ORDER BY order_id DESC";
            page = this.baseDaoSupport.queryForPage(sql, pageNo,
                    pageSize, ApiOrderList.class, status, member_id);
        } else {
            sql = "select * from order where " + filedname
                    + " in (" + status + ") AND member_id=? AND parent_id is NOT NULL ORDER BY order_id DESC";
            page = this.baseDaoSupport.queryForPage(sql, pageNo,
                    pageSize, ApiOrderList.class, member_id);

        }

        List<ApiOrderList> orderlist = (List<ApiOrderList>) page.getResult();
        for (ApiOrderList order : orderlist) {
            Integer number = this.baseDaoSupport.queryForInt("select sum(num) from es_order_items ot left join es_order o on "
                    + " o.order_id=ot.order_id where o.order_id =? or o.parent_id=?", order.getOrder_id(), order.getOrder_id());
            order.setGoods_num(number);
        }
        return page;
    }

    @Override
    public Page getMystorage(long memberId, int pageNo, int pageSize) {
        String sql = " select order_id,sn,start_storage_time,end_storage_time ,items_json"
                + "  from es_order  where parent_id is not null and is_storage =1 and member_id =? order by order_id DESC ";
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, memberId);
    }

    @Override
    public void getWine(long member_id, Integer order_id) {

        this.daoSupport.execute("update es_order set is_storage=0,end_storage_time=? where member_id=? and "
                + " order_id =?", DateUtil.getDateline(), member_id, order_id);

    }

    /**
     * 更新订单状态
     * 活动中30分钟不付款 订单作废
     * 普通订单24小时不付款 订单作废
     * 发货15天 没收货 自动确认收货
     */
    public void updateStatus() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1); //1天
        long date1 = (cal.getTime().getTime() / 1000);
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -30); //30分钟
        long date2 = (cal.getTime().getTime() / 1000);

        this.daoSupport.execute("update es_order set status = ? where ((create_time < ? and activities = 0) or (create_time < ? and activities > 0)) and pay_status = ? and status = ?",
                OrderStatus.ORDER_CANCELLATION, date1, date2, OrderStatus.PAY_NO, OrderStatus.ORDER_NOT_PAY);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -15); //15天
        long date3 = (cal.getTime().getTime() / 1000);
        List<Map> list = daoSupport.queryForList("select sn,order_id from es_order where sale_cmpl_time < ? and status = ? and parent_id is not null", date3, OrderStatus.ORDER_SHIP);

        //用户点确认收货的时候 SIGNING_TIME 是这个时间
        //SALE_CMPL_TIME 这个是发货时间
        //COMPLETE_TIME 目前应该用不到了, 用于用户确认收获之后15天发放积分
        //signing_time妥投时间
        this.daoSupport.execute("update es_order set signing_time =? where sale_cmpl_time < ? and status = ?",
                date3, date3, OrderStatus.ORDER_SHIP);

        //仅通知,上面获取的这个列表有问题
        for (Map map : list) {
            System.out.println("orderManager-----------------------");
            String sn = (String)map.get("sn");
            final Integer orderId = (Integer)map.get("order_id");
            erpManager.notifyOmsForDefault(sn);
//            this.daoSupport.execute("update es_order set status =? where sn = ?", OrderStatus.ORDER_COMPLETE, sn);
            //通知更新商品已售数量
            this.updateGoodsStatus(orderId);
        }
    }

    /**
     * <p> 15天自动收货后，需要更新商品的售出数量 <br/>
     * 创建时间：2016/9/29 17:19<br/>
     * 创建者： handaquan<br/>
     * 修改记录：<br/>
     *       yyyy/mm/dd 成员拼音：修改内容<br/>
     * </p>
     * @param
     * @return
     * @throws
     */
    private void updateGoodsStatus(Integer orderId) {
        ArrayList<Map> goodsList = (ArrayList) daoSupport.queryForList("select goods_id,num from es_order_items where order_id = ?", orderId);
        if (goodsList.size() > 0) {
            for (Map result : goodsList) {
                Integer goodsNum = (Integer) result.get("num");
                Integer goodId = (Integer) result.get("goods_id");
                if (goodsNum != 0 && goodId != 0) {
                    String sql2 = "update es_goods set buy_count=buy_count + " + goodsNum + " where goods_id =" + goodId;
                    log.debug("[15天后更新商品已售数量]-sql=" + sql2);
                    daoSupport.execute(sql2);
                    //删除缓存
                    iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
                    iCache.remove(String.valueOf(goodId));
                }
            }
        }
    }

}
	
	
