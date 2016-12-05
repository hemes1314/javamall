/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：订单Api
 * 修改人：
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.erp;

import com.enation.app.b2b2c.core.action.backend.order.StoreOrderAction;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.*;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.model.*;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.*;
import com.enation.app.shop.core.service.impl.OrderManager;
import com.enation.app.shop.mobile.model.ApiOrderList;
import com.enation.app.shop.mobile.service.impl.ErpManager;
import com.enation.app.shop.mobile.util.ErpActionUtils;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.*;
import com.enation.framework.util.web.ServletUtils;
import com.enation.framework.util.web.Struts2Utils;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * ERP Api, 仅和erp & OMS交互
 *
 * @author Ken 20160321
 * @version v1.0
 * @since v1.0
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("erp")
public class ErpApiAction extends WWAction {
    private Log log = LogFactory.getLog(ErpApiAction.class);
    private IOrderManager orderManager;
    private IMemberAddressManager memberAddressManager;
    private IPaymentManager paymentManager;
    private ICartManager cartManager;
    private IDlyTypeManager dlyTypeManager;
    private IMemberOrderManager memberOrderManager;
    private IReceiptManager receiptManager;
    private IOrderFlowManager orderFlowManager;
    private IMemberManager memberManager;
    private IStoreCartManager storeCartManager;
    private IStoreMemberAddressManager storeMemberAddressManager;
    private IStoreTemplateManager storeTemplateManager;
    private IStoreDlyTypeManager storeDlyTypeManager;
    private IStoreOrderManager storeOrderManager;
    private IBonusManager bonusManager;
    private CartPluginBundle cartPluginBundle;
    private IProductManager productManager;
    private IStoreBonusManager storeBonusManager;
    private IGoodsManager goodsManager;
    private IRegionsManager regionsManager;
    private IDaoSupport daoSupport;
    private IMemberPointManger memberPointManger;
    private ISellBackManager sellBackManager;
    
    public ISellBackManager getSellBackManager() {
        return sellBackManager;
    }

    
    public void setSellBackManager(ISellBackManager sellBackManager) {
        this.sellBackManager = sellBackManager;
    }

    private ErpManager erpManager;


    private final int PAGE_SIZE = 20;
    //支付
    private String order_sn;
    private Double money;
    private String mark;

    //余额支付
    private Double advancePay;
    //erp
    private String order_state;
    private String status;
    private String paystatus;
    private String shipstatus;
    private String shipping_type;
    // 红包
    private Integer order_id;
    private Integer bonus_id;
    private Integer regionid;
    //店铺优惠券
    private Integer storeBonusId;
    private Integer storeId;
    private StoreOrderAction storeOrderAction;

    //余额支付
    private int useBalance;
    
//    public void updatesttest(){
//        Integer[] productIdArray = new Integer[2];
//        productIdArray[0] = 7524;
//        productIdArray[1] = 7526;
//        orderManager.updateErpStatus(3, productIdArray);
//    }
    
    public String returnChildorder(){
        HttpServletRequest request = getRequest();
        try {
            String body = ErpActionUtils.extractPostRequestBody(request);
            SellBackList so = this.sellBackManager.get(body);
            if(so==null){
                return null;
            }else{
                Struts2Utils.renderText(so.getOrdersn());
            }
        }catch(Exception e){
            logger.error("", e);
        }
        return null;
    }
    
    public String listOrders() {
        //如果是买a赠b wms：两条数据; erp：两条数据
        //如果是买a赠a wms：一条数据; erp：两条数据
        //目前因为没有赠品一说,所以不考虑

        int MAX_RESULT = 1;

        Document document = null;
        try {
            if (Objects.equals(status, "0")) {
                document = ErpActionUtils.createDefaultErpXMLNoDataResponse();
                String xml = document.asXML();
                Struts2Utils.renderXml(xml);
                return null;
            }

            Map<Order, List<StoreOrder>> map = storeOrderManager.listForErp(MAX_RESULT);
            Set<Integer> productIds = new HashSet<>();
            for (Map.Entry<Order, List<StoreOrder>> e : map.entrySet()) {
                Order mo = e.getKey();
                List<StoreOrder> soList = e.getValue();

                for (StoreOrder so : soList) {
                    List<OrderItem> items = so.getOrderItemList();
                    for (OrderItem item : items) {
                        if (item.getProduct_id() != null)
                            productIds.add(item.getProduct_id());
                    }
                }
            }

            Integer[] productIdArray = new Integer[productIds.size()];
            int i = 0;
            for (Integer pid : productIds) {
                productIdArray[i++] = pid;
            }
            Map<Integer,Double> weightMap = storeOrderManager.getProductWeightMap(productIdArray);

            if (!map.isEmpty()) {
                document = ErpActionUtils.createDefaultErpXMLResponse();
                document.getRootElement().element("hasData").elementTextTrim("T");
                Element wmsRoot = document.getRootElement().element("Wms").element("root").element("SalesOrderReceivings");
                Element erpRoot = document.getRootElement().element("Erp").element("data").element("entry");
                ErpActionUtils.appendOrderWmsElement(wmsRoot, map, regionsManager);
                ErpActionUtils.appendOrderErpElement(erpRoot, map, regionsManager, weightMap);
            } else {
                document = ErpActionUtils.createDefaultErpXMLNoDataResponse();
            }

            String xml = document.asXML();
            Struts2Utils.renderXml(xml);
        } catch (Exception e) {
            logger.error("", e);
            String xml = ErpActionUtils.createDefaultXMLStringResponse(false, e.getMessage(), null);
            Struts2Utils.renderXml(xml);
        }

        return null;
    }

    /**
     * 2更新订单状态接口
     * 1销售出库  2退货入库(退货单号)  3拒收入库(小单号)
     * @return
     */
    public String updateOrderStatus() {
        HttpServletRequest request = getRequest();
        try {
            Document doc = ErpActionUtils.parseRequest(request);
            Element root = doc.getRootElement();
            int status = 0;
            status = NumberUtils.toInt(root.element("status").getTextTrim()); //状态值

            Integer[] ids = null;

            List<Element> orderMessages = root.elements("orderMessage");
            List<String> orderSnList = new ArrayList<>();
            for (Element el : orderMessages) {
                orderSnList.add(el.elementTextTrim("orderid"));
            }
            if(status==3 || status==1000){
                ids = new Integer[orderSnList.size()];
                for (int i = 0; i < orderSnList.size(); i++) {
                    StoreOrder so = storeOrderManager.get(orderSnList.get(i));
                    ids[i] = so.getOrder_id();//订单 id
                }
            }else if(status==2){
                ids = new Integer[orderSnList.size()];
                for (int i = 0; i < orderSnList.size(); i++) {
                    SellBackList so = this.sellBackManager.get(orderSnList.get(i));
                    ids[i] = so.getOrderid();//订单 id
                }
            }

            if (OrderStatus.ORDER_THOUSAND == status) { //1000 的时候 拉走
                orderManager.updateErpFlag(ids);
            } else {
                orderManager.updateErpStatus(status, ids);
            }
            String xml = ErpActionUtils.createDefaultXMLStringResponse(true, null, null);
            Struts2Utils.renderXml(xml);
        } catch (Exception e) {
            logger.error("", e);
            String xml = ErpActionUtils.createDefaultXMLStringResponse(false, e.getMessage(), null);
            Struts2Utils.renderXml(xml);
        }

        return null;
    }

    /**
     * 3确认发货接口
     */
    public String updateShippingStatus() {

        HttpServletRequest request = getRequest();
        try {
            Document doc = ErpActionUtils.parseRequest(request);
            log.info("updateShippingStatus:"+doc.getXMLEncoding());
            List<Element> items = doc.getRootElement().element("body").elements("entry");
            this.orderManager.updateShipStatus(items);
            String xml = ErpActionUtils.createDefaultXMLStringResponse(true, null, null);
            Struts2Utils.renderXml(xml);
        } catch (Exception e) {
            logger.error("", e);
            String xml = ErpActionUtils.createDefaultXMLStringResponse(false, e.getMessage(), null);
            Struts2Utils.renderXml(xml);
        }

        return null;
    }


    /**
     * 4更新商品库存接口: 多店的话以es_product的库存为准
     *
     * @return
     */
    public String updateStock() {
        HttpServletRequest request = getRequest();
        try {
            Document doc = ErpActionUtils.parseRequest(request);
            System.out.println(doc.asXML());
            List<Element> items = doc.getRootElement().element("goods").elements("good");
            erpManager.updateStock(items);

            String xml = ErpActionUtils.createDefaultXMLStringResponse(true, null, null);
            Struts2Utils.renderXml(xml);
        } catch (Exception e) {
            logger.error("", e);
            String xml = ErpActionUtils.createDefaultXMLStringResponse(false, e.getMessage(), null);
            Struts2Utils.renderXml(xml);
        }

        return null;
    }

    public String test() {
        //erpManager.notifyOmsForDefault("1", "2");
        //erpManager.notifyOmsForRefund("3", "4", "5");
        Order order = storeOrderManager.get("145924078613");
        boolean ret = erpManager.notifyOmsForRefund(order,"");
        System.out.println(":::" + ret);

        return null;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getOrder_state() {
        return order_state;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(String paystatus) {
        this.paystatus = paystatus;
    }

    public String getShipstatus() {
        return shipstatus;
    }

    public void setShipstatus(String shipstatus) {
        this.shipstatus = shipstatus;
    }

    public String getShipping_type() {
        return shipping_type;
    }

    public void setShipping_type(String shipping_type) {
        this.shipping_type = shipping_type;
    }

    public IOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public IMemberAddressManager getMemberAddressManager() {
        return memberAddressManager;
    }

    public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
        this.memberAddressManager = memberAddressManager;
    }

    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
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

    public IMemberOrderManager getMemberOrderManager() {
        return memberOrderManager;
    }

    public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
        this.memberOrderManager = memberOrderManager;
    }

    public IReceiptManager getReceiptManager() {
        return receiptManager;
    }

    public void setReceiptManager(IReceiptManager receiptManager) {
        this.receiptManager = receiptManager;
    }

    public IOrderFlowManager getOrderFlowManager() {
        return orderFlowManager;
    }

    public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
        this.orderFlowManager = orderFlowManager;
    }

    public IMemberManager getMemberManager() {
        return memberManager;
    }

    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public IStoreCartManager getStoreCartManager() {
        return storeCartManager;
    }

    public void setStoreCartManager(IStoreCartManager storeCartManager) {
        this.storeCartManager = storeCartManager;
    }

    public IStoreMemberAddressManager getStoreMemberAddressManager() {
        return storeMemberAddressManager;
    }

    public void setStoreMemberAddressManager(
            IStoreMemberAddressManager storeMemberAddressManager) {
        this.storeMemberAddressManager = storeMemberAddressManager;
    }

    public IStoreTemplateManager getStoreTemplateManager() {
        return storeTemplateManager;
    }

    public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
        this.storeTemplateManager = storeTemplateManager;
    }

    public IStoreDlyTypeManager getStoreDlyTypeManager() {
        return storeDlyTypeManager;
    }

    public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
        this.storeDlyTypeManager = storeDlyTypeManager;
    }

    public IStoreOrderManager getStoreOrderManager() {
        return storeOrderManager;
    }

    public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
        this.storeOrderManager = storeOrderManager;
    }

    public CartPluginBundle getCartPluginBundle() {
        return cartPluginBundle;
    }

    public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
        this.cartPluginBundle = cartPluginBundle;
    }

    public IProductManager getProductManager() {
        return productManager;
    }

    public void setProductManager(IProductManager productManager) {
        this.productManager = productManager;
    }

    public IBonusManager getBonusManager() {
        return bonusManager;
    }

    public void setBonusManager(IBonusManager bonusManager) {
        this.bonusManager = bonusManager;
    }

    private StoreCartPluginBundle storeCartPluginBundle;


    public IStoreBonusManager getStoreBonusManager() {
        return storeBonusManager;
    }


    public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
        this.storeBonusManager = storeBonusManager;
    }


    public Integer getBonus_id() {
        return bonus_id;
    }


    public void setBonus_id(Integer bonus_id) {
        this.bonus_id = bonus_id;
    }


    public Integer getRegionid() {
        return regionid;
    }


    public void setRegionid(Integer regionid) {
        this.regionid = regionid;
    }


    public StoreOrderAction getStoreOrderAction() {
        return storeOrderAction;
    }


    public void setStoreOrderAction(StoreOrderAction storeOrderAction) {
        this.storeOrderAction = storeOrderAction;
    }


    public StoreCartPluginBundle getStoreCartPluginBundle() {
        return storeCartPluginBundle;
    }


    public void setStoreCartPluginBundle(StoreCartPluginBundle storeCartPluginBundle) {
        this.storeCartPluginBundle = storeCartPluginBundle;
    }

    public String getOrder_sn() {
        return order_sn;
    }


    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }


    public Double getMoney() {
        return money;
    }


    public void setMoney(Double money) {
        this.money = money;
    }


    public String getMark() {
        return mark;
    }


    public void setMark(String mark) {
        this.mark = mark;
    }


    public Double getAdvancePay() {
        return advancePay;
    }


    public void setAdvancePay(Double advancePay) {
        this.advancePay = advancePay;
    }


    public Integer getStoreBonusId() {
        return storeBonusId;
    }


    public void setStoreBonusId(Integer storeBonusId) {
        this.storeBonusId = storeBonusId;
    }


    public Integer getStoreId() {
        return storeId;
    }


    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }


    public int getUseBalance() {
        return useBalance;
    }


    public void setUseBalance(int useBalance) {
        this.useBalance = useBalance;
    }


    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }


    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }


    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }


    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }


    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }


    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }


    public IMemberPointManger getMemberPointManger() {
        return memberPointManger;
    }


    public void setMemberPointManger(IMemberPointManger memberPointManger) {
        this.memberPointManger = memberPointManger;
    }

    public void setErpManager(ErpManager erpManager) {
        this.erpManager = erpManager;
    }

    public ErpManager getErpManager() {
        return erpManager;
    }


}
