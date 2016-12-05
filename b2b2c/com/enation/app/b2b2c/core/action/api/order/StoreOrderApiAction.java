package com.enation.app.b2b2c.core.action.api.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.impl.StoreOrderManager;
import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderPrintManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.impl.CartManager;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 店铺订单API
 *
 * @author LiFenlong
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeOrder")
public class StoreOrderApiAction extends WWAction {
    private IOrderManager orderManager;
    @Autowired
    private StoreOrderManager storeOrderManager;
    private IOrderFlowManager orderFlowManager;
    private IMemberAddressManager memberAddressManager;
    private IOrderPrintManager orderPrintManager;
    private IStoreCartManager storeCartManager;
    private IStoreMemberManager storeMemberManager;
    private IStoreDlyTypeManager storeDlyTypeManager;
    @Autowired
    private CartManager cartManager;
    private StoreCartPluginBundle storeCartPluginBundle;
    private IBonusManager bonusManager;
    private IPaymentManager paymentManager;
    private IRegionsManager regionsManager;
    private IBonusTypeManager bonusTypeManager;
    @Autowired
    private CostDownActiveManager costDownActiveManager;

    private Integer orderId;
    private Integer[] order_id;
    private Integer paymentId;
    private Double payMoney;
    private Double shipmoney;
    private String remark;
    private String ship_day;
    private String ship_name;
    private String ship_tel;
    private String ship_mobile;
    private String ship_zip;
    private String storeids;
    private String shippingids;
    private Integer regionid;
    private String addr;
    private String[] shipNos;
    private String bonusid;
    private Integer[] logi_id;
    private String[] logi_name;

    //店铺id
    private int store_id;

    //配送方式模板id
    private int type_id;

    //收货地址id
    private int address_id;

    //优惠券id
    private int bonus_id;

    //余额支付
    private int useBalance;

    //是否执意提交（购物车商品发生改变）如果为yes表示不进行购物车的判断
    private String firmlySubmit = "no";
    private String needPayOrder; //需要支付的总费用
    private String[] goodsIds; //商品id
    private String[] nums; //商品数量
    private String[] prices;//商品价格
    private Integer[] costDownIds;//活动id集合
    private String cartStoreIds; //购物车中店铺id集合，以，分隔
    
    @Value("#{configProperties['order.address.kuaidi.post.url']}")
    private String orderAddressKuaidiPostUrl;

    /**
     * 创建订单，需要购物车中有商品
     *
     * @param address_id:收货地址id.int型，必填项
     * @param payment_id:支付方式id，int型，必填项
     * @param shipDay：配送时间，String型       ，可选项
     * @param shipTime，String型           ，可选项
     * @param remark，String型             ，可选项
     * @return 返回json串
     * result  为1表示添加成功0表示失败 ，int型
     * message 为提示信息
     */
    public String create() {
        try {
            if (goodsIds == null || nums == null) {
                this.showErrorJson("请选择商品！");
                return this.JSON_MESSAGE;
            }

            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            List<Map<String, Object>> goods = storeCartManager.listSelectedGoods();//获取数据库中最新数据

            if (goods != null) {
                if (goods.size() != goodsIds.length) {
                    this.json = JsonMessageUtil.getCartChangeJson("购物车中商品发生改变，请重新选择！");
                    return this.JSON_MESSAGE;
                }

                for (Map<String, Object> map : goods) {
                    if (map.get("session_id").equals(request.getSession().getId())) {
                        if (!isExist(NumberUtils.toInt(map.get("goods_id").toString()), NumberUtils.toInt(map.get("num").toString()), NumberUtils.toDouble(map.get("price").toString()))) {
                            this.json = JsonMessageUtil.getCartChangeJson("购物车中商品发生改变，请重新选择！");
                            return this.JSON_MESSAGE;
                        }
                    } else {
                        this.json = JsonMessageUtil.getCartChangeJson("购物车中商品发生改变，请重新选择！");
                        return this.JSON_MESSAGE;
                    }

                }
            }

            if (costDownIds != null && costDownIds.length != 0) {
                //判断直降活动是否结束(活动结束，价格会发生变化)
                Set<Integer> set = new HashSet<Integer>(Arrays.asList(costDownIds));
                for (Integer costDownId : set) {
                    if (costDownId.intValue() == 0) {
                        continue;
                    }
                    CostDownActive costDownActive = costDownActiveManager.get(costDownId);
                    if (costDownActive == null) {
                        this.json = JsonMessageUtil.getCartChangeJson("购物车中商品发生改变，请重新选择！");
                        return this.JSON_MESSAGE;
                    } else {
                        if (costDownActive.getAct_status() == 2 || DateUtil.getDateline() < costDownActive.getStart_time() || DateUtil.getDateline() > costDownActive.getEnd_time()) {
                            this.json = JsonMessageUtil.getCartChangeJson("购物车中商品发生改变，请重新选择！");
                            return this.JSON_MESSAGE;
                        }
                    }
                }
            }

            Order order = this.innerCreateOrder();
            if (order == null)
            {
                this.json = JsonMessageUtil.getCartChangeJson("购物车中商品发生改变，请重新选择！");
                return this.JSON_MESSAGE;
            }
            this.json = JsonMessageUtil.getObjectJson(order, "order");
        } catch (RuntimeException e) {
            //e.printStackTrace();
            if (e.getMessage().equals("dly")) {
                this.json = JsonMessageUtil.getCheckoutRefreshJson("运费金额发生改变，请刷新页面！");
                return this.JSON_MESSAGE;
            } else {
                this.logger.error("创建订单", e);
                this.showErrorJson("创建订单异常: " + e.getMessage());
            }
        }
        return this.JSON_MESSAGE;
    }

    //根据商品id，数量,价格判断是否要提交的商品
    private boolean isExist(int goodsId, int num, double price) {
        for (int i = 0; i < goodsIds.length; i++) {
            if (NumberUtils.toInt(goodsIds[i]) == goodsId && NumberUtils.toInt(nums[i]) == num && NumberUtils.toDouble(prices[i]) == price) {
                return true;
            }
        }
        return false;
    }


    private Order innerCreateOrder() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();

        Integer shippingId = 0; //主订单没有配送方式

        Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"), 0);
        if (paymentId == 0) throw new RuntimeException("支付方式不能为空");

        Order order = new Order();
        order.setShipping_id(shippingId); //配送方式
        order.setPayment_id(paymentId);//支付方式

        //用户选中的地址
        MemberAddress address = StoreCartContainer.getUserSelectedAddress();

        if (address == null) {
            throw new RuntimeException("登录超时或无效提交！");
        }
        // 2015/10/23 humaodong
        if (address.getCity() == null || "请选择".equals(address.getCity())) address.setCity("");
        if (address.getRegion() == null || "请选择".equals(address.getRegion())) address.setRegion("");

        //如果是货到付款
        if (paymentManager.get(paymentId).getType().equals("cod")) {

            try {
                /********* 2015/12/23 humaodong ************/
                Integer regionId = address.getRegion_id();
                if (regionId == null || regionId == 0) regionId = address.getCity_id();
                if (regionId == null || regionId == 0) regionId = address.getProvince_id();
                /*******************************************/
                if (regionsManager.get(regionId).getCod() != 1) {
                    throw new RuntimeException("不支持货到付款");
                }
            } catch (Exception e) {
                //如果地区 被删除出现的查询异常，返回不支持货到付款
                throw new RuntimeException("不支持货到付款");
            }
        }
        order.setShip_provinceid(address.getProvince_id());
        order.setShip_cityid(address.getCity_id());
        order.setShip_regionid(address.getRegion_id());

        order.setShip_addr(address.getAddr());
        order.setShip_mobile(address.getMobile());
        order.setShip_tel(address.getTel());
        order.setShip_zip(address.getZip());
//        order.setShipping_area(address.getProvince() + "-" + address.getCity() + "-" + address.getRegion());
        order.setShipping_area(address.getProvince() + address.getCity() + address.getRegion());
        order.setShip_name(address.getName());
        order.setRegionid(address.getRegion_id());


        order.setMemberAddress(address);
        order.setShip_day(request.getParameter("shipDay"));
        //设置存酒 和存酒时间
        if (request.getParameter("shipDay").equals("存酒暂不送货")) {
            order.setIs_storage(1);
            order.setStart_storage_time(DateUtil.getDateline());
            order.setEnd_storage_time(DateUtil.getDateline() + 2592000); // 默认30天
            order.setStorage_date(30);
        }

        order.setShip_time(request.getParameter("shipTime"));
        order.setRemark(request.getParameter("remark"));
        order.setAddress_id(address.getAddr_id());
        String sessionid = request.getSession().getId();

        try {
            String[] bonusIdStrs = request.getParameterValues("bonusid");
            for (String str : bonusIdStrs) {
                int bonusId = Integer.valueOf(str.split("-")[0]);
                if (bonusId > 0)
                    order.getBonusIds().add(bonusId);
            }
        } catch (Exception e) {
        }
        //设置页面传递的支付金额，判断支付金额是否一致
        order.setNeed_pay_money(NumberUtils.toDouble(needPayOrder));

        order = storeOrderManager.createOrder(order, sessionid);

        return order;
    }


    /**
     * 改变店铺的配送方式以及红包<br>
     * 调用此api时必须已经访问过购物车列表<br>
     *
     * @return 含有价格信息的json串
     */
    public String changeArgsType() {

        //由购物车列表中获取此店铺的相关信息
        @SuppressWarnings("rawtypes")
        Map storeData = StoreCartContainer.getStoreData(store_id);

        //获取此店铺的购物列表
        List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());

        //配送地区
        String regionid_str = regionid == null ? "" : regionid + "";

        //计算此配送方式时的店铺相关价格
        OrderPrice orderPrice = this.cartManager.countPrice(list, type_id, regionid_str);

        //激发计算子订单价格事件
        orderPrice = storeCartPluginBundle.countChildPrice(orderPrice);
        if (bonus_id != 0) {
            MemberBonus bonuss = bonusManager.getBonus(bonus_id);
            changeBonus(orderPrice, bonuss, store_id);
        }

        //加运费 排除 满减
        Double needPayMoney = CurrencyUtil.add(orderPrice.getGoodsBasePrice(), orderPrice.getShippingPrice());
        needPayMoney = CurrencyUtil.sub(needPayMoney, orderPrice.getDiscountPrice());
        orderPrice.setNeedPayMoney(needPayMoney);
        orderPrice.setOrderPrice(needPayMoney);

        //重新压入此店铺的订单价格和配送方式id
        storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
        storeData.put(StoreCartKeyEnum.shiptypeid.toString(), type_id);

        this.json = JsonMessageUtil.getObjectJson(orderPrice, "storeprice");

        return this.JSON_MESSAGE;
    }

    //2015/10/13 humaodong
    public String useBonus() {
        if (bonus_id != 0) {
            MemberBonus bonus = bonusManager.getBonus(bonus_id);
            //不一定是可以用的,可能超过总价了

            BonusSession.useOne(bonus);
        } else {
            BonusSession.clean();
        }
        this.showPlainSuccessJson("ok");
        return this.JSON_MESSAGE;
    }

    //2015/10/26 humaodong
    public String useBalance() {
        if (useBalance == 0) {
            ThreadContextHolder.getSessionContext().removeAttribute("useBalance");
        } else {
            ThreadContextHolder.getSessionContext().setAttribute("useBalance", 1);
        }
        this.showPlainSuccessJson("ok");
        return this.JSON_MESSAGE;
    }

    /**
     * 修改优惠券选项
     *
     * @param bonuss
     * @author liushuai
     * @version v1.0, 2015年9月22日18:21:33
     * @since v1.0
     */
    private OrderPrice changeBonus(OrderPrice orderprice, MemberBonus bonus, int storeid) {

        // set 红包
        BonusSession.use(storeid, bonus);
        if (orderprice.getNeedPayMoney() <= bonus.getBonus_money()) {
            /******** 2015/10/16 humaodong ********/
            orderprice.setDiscountPrice(orderprice.getNeedPayMoney());
            /**************************************/
            orderprice.setNeedPayMoney(0.0);
        } else {
            // 计算需要支付的金额
            orderprice.setNeedPayMoney(CurrencyUtil.add(
                    orderprice.getNeedPayMoney(), -bonus.getBonus_money()));

            orderprice.setDiscountPrice(bonus.getBonus_money());
        }
        return orderprice;

    }

    /**
     * 改变收货地址<br>
     * 调用此api时会更改session中的用户选中的地址
     *
     * @return
     */
    public String changeAddress() {
        try {
            //根据id得到地址后压入session
            MemberAddress address = this.memberAddressManager.getAddress(address_id);
            StoreCartContainer.putSelectedAddress(address);

            //重新计算价格
            this.storeCartManager.countPrice("yes", true);

            //由session中获取店铺购物车数据，已经是计算过费用的了
            List<Map> storeCartList = StoreCartContainer.getStoreCartListFromSession();

            List newList = new ArrayList();
            for (Map map : storeCartList) {
                Map jsonMap = new HashMap();
                jsonMap.putAll(map);
                jsonMap.remove(StoreCartKeyEnum.goodslist.toString());
                newList.add(jsonMap);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", "1");
            jsonObject.put("data", newList);

            this.json = jsonObject.toJSONString();
        } catch (Exception e) {
            this.showErrorJson(e.getMessage());
        }


        return this.JSON_MESSAGE;
    }


    /**
     * 订单确认
     *
     * @param orderId 订单Id,Integer
     * @return 返回json串
     * result 	为1表示调用成功0表示失败
     */
    public String confirm() {
        try {
            this.orderFlowManager.confirmOrder(orderId);
            Order order = this.orderManager.get(orderId);
            //this.orderFlowManager.addCodPaymentLog(order);
            this.showSuccessJson("'订单[" + order.getSn() + "]成功确认'");
        } catch (RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
            this.showErrorJson("订单确认失败" + e.getMessage());
        }
        return this.JSON_MESSAGE;
    }


    /**
     * 订单支付
     *
     * @param orderId   订单Id,Integer
     * @param member    店铺会员,StoreMember
     * @param paymentId 结算单Id,Integer
     * @param payMoney  付款金额,Double
     * @return 返回json串
     * result 	为1表示调用成功0表示失败
     */
    public String pay() {
        try {
            //获取当前操作者
            StoreMember member = storeMemberManager.getStoreMember();
            Order order = this.orderManager.get(orderId);
            // 调用执行添加收款详细表
            if (orderFlowManager.pay(paymentId, orderId, payMoney, member.getUname())) {
                showSuccessJson("订单[" + order.getSn() + "]收款成功");
            } else {
                showErrorJson("订单[" + order.getSn() + "]收款失败,您输入的付款金额合计大于应付金额");
            }
        } catch (RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
            showErrorJson("确认付款失败:" + e.getMessage());
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 订单发货
     *
     * @param order_id 订单Id,Integer[]
     * @return 返回json串
     * result 	为1表示调用成功0表示失败
     */
    public String ship() {
        try {
            storeOrderManager.saveShipNo(order_id, logi_id[0], logi_name[0], shipNos[0]);
            String is_ship = orderPrintManager.ship(order_id);
            if (is_ship.equals("true")) {
            	if (order_id != null) {
            		Order order = null;
	            	for (Integer id : order_id) {
	            		order = orderManager.get(id);
	            		try {
	            	    	// 发货后请求快递100物流信息
	            	    	NameValuePair param = new BasicNameValuePair("sn", order.getSn());
	            	    	String result = HttpUtils.get(orderAddressKuaidiPostUrl, Arrays.asList(param));
	            	    	if (logger.isDebugEnabled()) {
	            	    		logger.debug("快递100订单物流信息请求查询结果: " + result);
	            	    	}
	            	    } catch (Exception e) {
	            	    	e.printStackTrace();
	            	    }
	            	}
            	}
                this.showSuccessJson("发货成功");
            } else {
                this.showErrorJson(is_ship);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson(e.getMessage());
            this.logger.error("发货：", e);
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 修改配送费用
     *
     * @param orderId        订单Id,Integer
     * @param currshipamount 修改前价格,Double
     * @param member         店铺会员,StoreMember
     * @return 返回json串
     * result 	为1表示调用成功0表示失败
     */
    public String saveShipPrice() {
        try {
            //修改前价格
            double currshipamount = orderManager.get(this.orderId).getShipping_amount();
            double price = this.orderManager.saveShipmoney(shipmoney, this.orderId);
            //获取操作人，记录日志
            StoreMember member = storeMemberManager.getStoreMember();
            //	this.orderManager.log(this.orderId, "运费从"+currshipamount+"修改为" + price, null, member.getUname());
            this.showSuccessJson("保存成功");
        } catch (RuntimeException e) {
            this.logger.error(e.getMessage(), e);
            this.showErrorJson("保存失败");
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 修改订单金额
     *
     * @param orderId 订单Id,Integer
     * @param amount  修改前价格,Double
     * @param member  店铺会员,StoreMember
     * @return 返回json串
     * result 	为1表示调用成功0表示失败
     */
    public String savePrice() {
        try {
            //修改前价格
            double amount = orderManager.get(this.orderId).getOrder_amount();
            this.orderManager.savePrice(payMoney, this.orderId);
            //获取操作人，记录日志
            StoreMember member = storeMemberManager.getStoreMember();
            //orderManager.log(this.orderId, "运费从"+amount+"修改为" + payMoney, null, member.getUname());
            this.showSuccessJson("修改订单价格成功");
        } catch (Exception e) {
            this.showErrorJson("修改订单价格失败");
            this.logger.error(e);
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 修改收货人信息
     *
     * @param orderId        订单Id,Integer
     * @param member         店铺会员,StoreMember
     * @param oldShip_day    修改前收货日期,String
     * @param oldship_name   修改前收货人姓名,String
     * @param oldship_tel    修改前收货人电话
     * @param oldship_mobile 修改前收货人手机号
     * @param remark         订单备注,String
     * @param ship_day       收货时间,String
     * @param ship_name      收货人姓名,String
     * @param ship_tel       收货人电话,String
     * @param ship_mobile    收货人手机号,String
     * @param ship_zip       邮政编号
     * @return 返回json串
     * result 	为1表示调用成功0表示失败
     */
    public String saveConsigee() {
        try {
            Order order = this.orderManager.get(this.getOrderId());
            StoreMember member = storeMemberManager.getStoreMember();
            String oldShip_day = order.getShip_day();
            String oldship_name = order.getShip_name();
            String oldship_tel = order.getShip_tel();
            String oldship_mobile = order.getShip_mobile();
            String oldship_zip = order.getShip_zip();

            boolean addr = this.storeOrderManager.saveShipInfo(remark, ship_day, ship_name, ship_tel, ship_mobile, ship_zip, this.getOrderId());
            //记录日志
            /*if(ship_day !=null && !StringUtil.isEmpty(ship_day)){
                this.orderManager.log(this.getOrderId(), "收货日期从['"+oldShip_day+"']修改为['" + ship_day+"']", null, member.getUname());
			}if(ship_name !=null && !StringUtil.isEmpty(ship_name)){
				this.orderManager.log(this.getOrderId(), "收货人姓名从['"+oldship_name+"']修改为['" + ship_name+"']", null,member.getUname());
			}if(ship_tel !=null && !StringUtil.isEmpty(ship_tel)){
				this.orderManager.log(this.getOrderId(), "收货人电话从['"+oldship_tel+"']修改为['" + ship_tel+"']", null,member.getUname());
			}if(ship_mobile !=null && !StringUtil.isEmpty(ship_mobile)){
				this.orderManager.log(this.getOrderId(), "收货人手机从['"+oldship_mobile+"']修改为['" + ship_mobile+"']", null,member.getUname());
			}if(ship_zip !=null && !StringUtil.isEmpty(ship_zip)){
				this.orderManager.log(this.getOrderId(), "收货人邮编从['"+oldship_zip+"']修改为['" + ship_zip+"']", null,member.getUname());
			}*/
            this.saveAddr();
            this.showSuccessJson("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("修改失败");
            logger.error(e);
        }
        return this.JSON_MESSAGE;
    }

    /**
     * 修改配送地区
     *
     * @param province    省,String
     * @param city        城市,String
     * @param region      区,String
     * @param Attr        详细地址,String
     * @param province_id 省Id,String
     * @param city_id     城市Id,String
     * @param region_id   区Id,String
     * @param oldAddr     修改前详细地址,String
     * @param orderId     订单Id,Integer
     * @return void
     */
    private void saveAddr() {
        //获取地区
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String region = request.getParameter("region");
        String Attr = province + "-" + city + "-" + region;
        //获取地区Id
        String province_id = request.getParameter("province_id");
        String city_id = request.getParameter("city_id");
        String region_id = request.getParameter("region_id");
        //记录日志，获取当前操作人
        String oldAddr = this.orderManager.get(this.orderId).getShip_addr();
        StoreMember member = storeMemberManager.getStoreMember();
        this.orderManager.saveAddr(this.orderId, StringUtil.toInt(province_id, true), StringUtil.toInt(city_id, true), StringUtil.toInt(region_id, true), Attr);
        this.orderManager.saveAddrDetail(this.getAddr(), this.getOrderId());

        //this.orderManager.log(this.orderId, "收货人详细地址从['"+oldAddr+"']修改为['" + this.getAddr()+"']", null, member.getUname());
    }

    /**
     * 会员地址
     *
     * @param address_id 地址Id,String
     * @return MemberAddress
     */
    private MemberAddress memberAddress() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String address_id = request.getParameter("addressId");
        return this.memberAddressManager.getAddress(Integer.valueOf(address_id));
    }

    /**
     * 获取订单价格信息
     *
     * @param storeid        店铺Id,String[]
     * @param typeid         类型,String[]
     * @param storeGoodsList 购物车商品列表, List<Map>
     * @param goodsprice     商品价格,Double
     * @param shippingprice  配送费用,Double
     * @param totleprice     打折费用,Double
     * @return json
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String getOrderPrice() {
        /*
        String [] storeid = storeids.split(",");
		String [] typeid = shippingids.split(",");
		String [] bonus =  bonusid.split(",");
		
		List<Map> storeGoodsList= new ArrayList<Map>();
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		storeGoodsList=storeCartManager.storeListGoods(sessionid);
		
		String storeprices="";
		String discountprice="";
		Double totle_discountprice=0.0;
		Double totleprice=0.0d;
		Double goodsprice=0.0d;
		Double shippingprice=0.0d;
		Double b2b2c_plateform_price=0.0d;
		
		for(Map map : storeGoodsList){
			Integer store_id=  (Integer) map.get("store_id");
			List list = (List) map.get("goodslist");
			for(int i=0;i<storeid.length;i++){
				if(store_id.equals(Integer.valueOf(storeid[i]))){
					Map maps = new HashMap();
					maps.put("storeid", store_id);
					maps.put("bonusid", Integer.valueOf(bonus[i]));
					OrderPrice orderPrice =  this.storeCartManager.countPrice(list, regionid+"", Integer.valueOf(typeid[i]), false,maps);
					//System.out.println(orderPrice.getNeedPayMoney());
					storeprices= storeprices+","+orderPrice.getOrderPrice();
					discountprice = discountprice+","+orderPrice.getDiscountPrice();
					totle_discountprice = CurrencyUtil.add(totle_discountprice, orderPrice.getDiscountPrice());
					totleprice = CurrencyUtil.add(totleprice, orderPrice.getNeedPayMoney());
					shippingprice= CurrencyUtil.add(shippingprice, orderPrice.getShippingPrice());
					goodsprice = CurrencyUtil.add(goodsprice,orderPrice.getGoodsPrice());
					b2b2c_plateform_price = orderPrice.getB2b2c_plateform_discountPrice();
					break;
				}
			}
		}
		storeprices = storeprices.substring(1, storeprices.length());
		discountprice = discountprice.substring(1, discountprice.length());
		Map pricemap = new HashMap();
		pricemap.put("result", 1);
		pricemap.put("storeprice", storeprices);
		pricemap.put("totleprice", CurrencyUtil.sub(totleprice, b2b2c_plateform_price));
		pricemap.put("goodsprice", goodsprice);
		pricemap.put("shippingprice", shippingprice);
		pricemap.put("discountprice", discountprice);
		pricemap.put("totle_discountprice",  CurrencyUtil.add(totle_discountprice, b2b2c_plateform_price));
		
		JSONArray jsons = JSONArray.fromObject(pricemap);
		this.json=jsons.toString().substring(1, jsons.toString().length()-1);*/

        return JSON_MESSAGE;
    }

    //set get
    public IStoreMemberManager getStoreMemberManager() {
        return storeMemberManager;
    }

    public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
        this.storeMemberManager = storeMemberManager;
    }

    public IMemberAddressManager getMemberAddressManager() {
        return memberAddressManager;
    }

    public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
        this.memberAddressManager = memberAddressManager;
    }

    public IOrderFlowManager getOrderFlowManager() {
        return orderFlowManager;
    }

    public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
        this.orderFlowManager = orderFlowManager;
    }

    public IOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public IOrderPrintManager getOrderPrintManager() {
        return orderPrintManager;
    }

    public void setOrderPrintManager(IOrderPrintManager orderPrintManager) {
        this.orderPrintManager = orderPrintManager;
    }

    public IStoreCartManager getStoreCartManager() {
        return storeCartManager;
    }

    public void setStoreCartManager(IStoreCartManager storeCartManager) {
        this.storeCartManager = storeCartManager;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer[] getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer[] order_id) {
        this.order_id = order_id;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public Double getShipmoney() {
        return shipmoney;
    }

    public void setShipmoney(Double shipmoney) {
        this.shipmoney = shipmoney;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShip_day() {
        return ship_day;
    }

    public void setShip_day(String ship_day) {
        this.ship_day = ship_day;
    }

    public String getShip_name() {
        return ship_name;
    }

    public void setShip_name(String ship_name) {
        this.ship_name = ship_name;
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

    public String getStoreids() {
        return storeids;
    }

    public void setStoreids(String storeids) {
        this.storeids = storeids;
    }

    public String getShippingids() {
        return shippingids;
    }

    public void setShippingids(String shippingids) {
        this.shippingids = shippingids;
    }

    public Integer getRegionid() {
        return regionid;
    }

    public void setRegionid(Integer regionid) {
        this.regionid = regionid;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String[] getShipNos() {
        return shipNos;
    }

    public void setShipNos(String[] shipNos) {
        this.shipNos = shipNos;
    }

    public String getBonusid() {
        return bonusid;
    }

    public void setBonusid(String bonusid) {
        this.bonusid = bonusid;
    }

    public Integer[] getLogi_id() {
        return logi_id;
    }

    public void setLogi_id(Integer[] logi_id) {
        this.logi_id = logi_id;
    }

    public String[] getLogi_name() {
        return logi_name;
    }

    public void setLogi_name(String[] logi_name) {
        this.logi_name = logi_name;
    }


    public int getStore_id() {
        return store_id;
    }


    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }


    public IStoreDlyTypeManager getStoreDlyTypeManager() {
        return storeDlyTypeManager;
    }


    public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
        this.storeDlyTypeManager = storeDlyTypeManager;
    }

    public StoreCartPluginBundle getStoreCartPluginBundle() {
        return storeCartPluginBundle;
    }


    public void setStoreCartPluginBundle(StoreCartPluginBundle storeCartPluginBundle) {
        this.storeCartPluginBundle = storeCartPluginBundle;
    }


    public int getType_id() {
        return type_id;
    }


    public void setType_id(int type_id) {
        this.type_id = type_id;
    }


    public int getAddress_id() {
        return address_id;
    }


    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }


    public int getBonus_id() {
        return bonus_id;
    }


    public void setBonus_id(int bonus_id) {
        this.bonus_id = bonus_id;
    }

    public int getUseBalance() {
        return useBalance;
    }

    public void setUseBalance(int useBalance) {
        this.useBalance = useBalance;
    }


    public IBonusManager getBonusManager() {
        return bonusManager;
    }


    public void setBonusManager(IBonusManager bonusManager) {
        this.bonusManager = bonusManager;
    }


    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }


    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }


    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }


    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }


    public IBonusTypeManager getBonusTypeManager() {
        return bonusTypeManager;
    }

    public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
        this.bonusTypeManager = bonusTypeManager;
    }

    public String getFirmlySubmit() {
        return firmlySubmit;
    }

    public void setFirmlySubmit(String firmlySubmit) {
        this.firmlySubmit = firmlySubmit;
    }

    public String getNeedPayOrder() {
        return needPayOrder;
    }

    public void setNeedPayOrder(String needPayOrder) {
        this.needPayOrder = needPayOrder;
    }

    public String[] getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String[] goodsIds) {
        this.goodsIds = goodsIds;
    }

    public String[] getNums() {
        return nums;
    }

    public void setNums(String[] nums) {
        this.nums = nums;
    }

    public Integer[] getCostDownIds() {
        return costDownIds;
    }

    public void setPrices(String[] prices) {
        this.prices = prices;
    }

    public String[] getPrices() {
        return prices;
    }

    public void setCostDownIds(Integer[] costDownIds) {
        this.costDownIds = costDownIds;
    }

    public void setCartStoreIds(String cartStoreIds) {
        this.cartStoreIds = cartStoreIds;
    }

    public String getCartStoreIds() {
        return cartStoreIds;
    }

    public static void main(String[] args) {

        String[] a = {"1", "23", "2", "1", "2"};
        Set<String> set = new HashSet<String>(Arrays.asList(a));
        System.out.println(set.size());

        System.out.println(DateUtil.getDateline());
    }
}
