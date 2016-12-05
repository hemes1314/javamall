package com.enation.app.b2b2c.core.tag.order;

import java.util.Map;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 查看店铺订单详细标签
 *
 * @author LiFenLong
 */
@Component
public class StoreOrderDetailTag extends BaseFreeMarkerTag {
    private IOrderManager orderManager;
    private IStoreOrderManager storeOrderManager;

    @Override
    protected Object exec(Map params) throws TemplateModelException {
        Integer orderid = (Integer) params.get("orderid");
        String ordersn = (String) params.get("ordersn");

        if (orderid == null && StringUtil.isEmpty(ordersn)) {
            throw new TemplateModelException("必须传递orderid参数或ordersn参数");
        }

        StoreOrder order = null;

        if (orderid != null) {
            order = storeOrderManager.get(orderid);
        } else if (!StringUtil.isEmpty(ordersn)) {
            order = storeOrderManager.get(ordersn);
        }

        if (order != null && order.getParent_id() != null) {
            Order parentOrder = orderManager.get(order.getParent_id());
            order.setParentOrder(parentOrder);
        }

        return order;
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
}
