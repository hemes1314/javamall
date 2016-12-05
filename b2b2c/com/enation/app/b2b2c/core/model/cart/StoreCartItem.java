package com.enation.app.b2b2c.core.model.cart;

import org.apache.commons.lang3.StringUtils;

import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.support.CartItem;

/**
 * b2b2c 购物车商品
 * 
 * @author LiFenLong
 *
 */
public class StoreCartItem extends CartItem implements Comparable<StoreCartItem> {

    private Integer store_id; //店铺Id
    private String store_name; //店铺名称
    private int goods_transfee_charge; //是否由卖家承担运费（即免运费）
    private String disable; // add by lxl
    private String market_enable; //add by lxl 
    private String storeNum; // add by lxl
    private int costDownId; //直降活动id

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public int getGoods_transfee_charge() {
        return goods_transfee_charge;
    }

    public void setGoods_transfee_charge(int goods_transfee_charge) {
        this.goods_transfee_charge = goods_transfee_charge;
    }

    public String getDisable() {
        return disable;
    }

    public void setDisable(String disable) {
        this.disable = disable;
    }

    public String getMarket_enable() {
        return market_enable;
    }

    public void setMarket_enable(String market_enable) {
        this.market_enable = market_enable;
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
    }
    
    public int getCostDownId() {
        return costDownId;
    }
    public void setCostDownId(int costDownId) {
        this.costDownId = costDownId;
    }

    @Override
    public int compareTo(StoreCartItem o) {
        Integer a = 0;
        Integer b = 0;
        if (!StringUtils.isBlank(this.getActivityName()))
            a += 10;
        if (!StringUtils.isBlank(o.getActivityName()))
            b += 10;
        if (this.getCurrent_activity() == Cart.TYPE_AVDBUY)
            a += 5;
        else if (this.getCurrent_activity() == Cart.TYPE_GROUPBUY)
            a += 4;
        else if (this.getCurrent_activity() == Cart.TYPE_COST_DOWN)
            a += 3;
        if (o.getCurrent_activity() == Cart.TYPE_AVDBUY)
            b += 5;
        else if (o.getCurrent_activity() == Cart.TYPE_GROUPBUY)
            b += 4;
        else if (o.getCurrent_activity() == Cart.TYPE_COST_DOWN)
            b += 3;
        return a.compareTo(b);
    }

}
