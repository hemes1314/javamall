package com.enation.app.b2b2c.core.model.order;

import com.enation.app.shop.core.model.SellBackList;
/**
 * 店铺退货单
 * @author fenlongli
 *
 */
public class StoreSellBackList extends SellBackList{

    private Integer store_id;   //店铺Id
    private int bill_status;        //结算状态
    private String bill_sn;         //结算编号

    public int getBill_status() {
        return bill_status;
    }

    public void setBill_status(int bill_status) {
        this.bill_status = bill_status;
    }

    public String getBill_sn() {
        return bill_sn;
    }

    public void setBill_sn(String bill_sn) {
        this.bill_sn = bill_sn;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }
    
}
