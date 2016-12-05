package com.enation.app.shop.mobile.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.openapi.service.IWareOpenApiManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.mobile.util.ErpActionUtils;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.DateUtil;

/**
 * Created by ken on 16/3/25.
 */
@Component
public class ErpManager extends BaseSupport {
    private final static Logger logger = LoggerFactory.getLogger(ErpManager.class);

    @Value("#{configProperties['address.oms']}")
    private String omsRestUrl;

    private IStoreOrderManager storeOrderManager;

    private IRegionsManager regionsManager;
    
    private IWareOpenApiManager wareOpenApiManager;

    /**
     * 直接根据sn更新自营店product表的库存
     *
     * @param goodsList
     */
    @Transactional
    public void updateStock(List<Element> goodsList) {

        List<Product> products = new ArrayList<Product>(goodsList.size());
        Product product = null;
        Map map = null;
        for (Element el : goodsList) {
            String sn = el.element("sku").getTextTrim();
            int count = NumberUtils.toInt(el.element("inventory").getTextTrim());
            //warehouse_id, warehouse_name are ignored

            logger.info("updating product with sn:{} store to {}", sn, count);
            baseDaoSupport.execute("UPDATE (select p.store as store,p.enable_store as enable_store from ES_PRODUCT p,ES_GOODS g where p.goods_id=g.goods_id and g.sn=? and g.store_name='自营店') t SET t.enable_store=?-t.store+t.enable_store, t.store=?", sn, count, count);
            baseDaoSupport.execute("update ES_GOODS set enable_store=?-store+enable_store, store=? where store_name='自营店' and sn=?",count,count,sn);
            
            map = baseDaoSupport.queryForMap("SELECT t1.*,t2.store_id FROM es_product t1 INNER JOIN es_goods t2 ON t2.goods_id=t1.goods_id WHERE t2.store_name='自营店' AND t2.sn=?", sn);
            if (map != null) {
                product = new Product();
                product.setProduct_id((Integer) map.get("product_id"));
                product.setEnable_store((Integer) map.get("enable_store"));
                product.setPrice((Double) map.get("price"));
                product.setStoreId((Integer) map.get("store_id"));
                products.add(product);
            }
        }
        // 调用商品可用库存修改OpenApi接口
        try {
            wareOpenApiManager.batchUpdateStock(products);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean notifyOmsForDefault(String orderSn) {
        return ErpActionUtils.requestOmsForDefault(orderSn, omsRestUrl);

    }
    private static final String DATETIME_FORMAT1 = "yyyy-MM-dd HH:mm:ss:SSS";
    public boolean notifyOmsForRefund(Order order,String tradeno) {
        Document document = ErpActionUtils.createDefaultErpRefundXMLResponse();
        document.getRootElement().addElement("OperTime").setText(DateUtil.toString(new Date(), DATETIME_FORMAT1)); //报文生成时间（精确到毫秒)
        Element root = document.getRootElement().element("SalesOrderReceivings");
        List<OrderItem> items = storeOrderManager.getOmsRefundOrderItems(order);
        try {
            ErpActionUtils.appendOrderRefundElement(root, order, items, regionsManager,tradeno);
            return ErpActionUtils.requestOmsForRefund(document.asXML(), omsRestUrl);
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }

    public void test() {
        /*
        List<Map> list = daoSupport.queryForList("select sn from es_order where order_id > 3940 and order_id < 3950 and parent_id is not null");
        for (Map map : list) {
            String sn = (String)map.get("sn");
            System.out.println(sn);
        }//*/
    }

    public IStoreOrderManager getStoreOrderManager() {
        return storeOrderManager;
    }

    public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
        this.storeOrderManager = storeOrderManager;
    }

    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }

    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }

    
    public void setWareOpenApiManager(IWareOpenApiManager wareOpenApiManager) {
        this.wareOpenApiManager = wareOpenApiManager;
    }
}
