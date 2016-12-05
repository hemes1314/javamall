package com.enation.app.shop.core.model;

import com.enation.framework.database.DynamicField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 购物车实体
 * 
 * @author kingapex 2010-3-23下午03:34:04
 */
public class Cart extends DynamicField {

    /**
     * 没有活动
     */
    public static int TYPE_NO_ACTIVITY = 0;

    /**
     * 闪购
     */
    public static int TYPE_GROUPBUY = 1;

    /**
     * 预售
     */
    public static int TYPE_AVDBUY = 2;

    /**
     * 直降
     */
    public static int TYPE_COST_DOWN = 3;

    private Integer cart_id;

    private Integer goods_id;

    private Integer product_id;

    private Integer num;

    private Double weight;

    private String session_id;

    private Integer itemtype;

    private String name;

    private Double price;

    //当前活动 
    private int current_activity = TYPE_NO_ACTIVITY;

    //当前活动名称
    private String current_activity_name = "";

    private String addon; //附件字串

    private Long member_id;

    @PrimaryKeyField
    public Integer getCart_id() {
        return cart_id;
    }

    public void setCart_id(Integer cartId) {
        cart_id = cartId;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer productId) {
        product_id = productId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String sessionId) {
        session_id = sessionId;
    }

    public Integer getItemtype() {
        return itemtype;
    }

    public void setItemtype(Integer itemtype) {
        this.itemtype = itemtype;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAddon() {
        return addon;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public int getCurrent_activity() {
        return current_activity;
    }

    public void setCurrent_activity(int current_activity) {
        this.current_activity = current_activity;
    }

    public String getCurrent_activity_name() {
        return current_activity_name;
    }

    public void setCurrent_activity_name(String current_activity_name) {
        this.current_activity_name = current_activity_name;
    }

}
