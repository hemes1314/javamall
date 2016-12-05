package com.enation.app.b2b2c.core.model.Drools;

import java.util.ArrayList;
import java.util.List;

public class OrderProductsActivity {

    private List<Product> list;

    // 订单活动列表
    private List<PromoActivity> activits = new ArrayList<PromoActivity>();

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }

    public List<PromoActivity> getActivits() {
        return activits;
    }

    public void setActivits(List<PromoActivity> activits) {
        this.activits = activits;
    }

}
