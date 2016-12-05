package com.enation.app.shop.core.model.support;

import java.util.HashMap;
import java.util.Map;

import com.enation.app.b2b2c.core.model.Drools.PromoActivity.DiscountItem;
import com.enation.framework.util.CurrencyUtil;

/**
 * 订单价格信息实体
 * 
 * @author kingapex 2010-5-30上午11:58:33
 * 
 */
public class OrderPrice {

    //商品价格
    private Double goodsBasePrice;
    
    //商品价格，经过优惠过的
    private Double goodsPrice;

    //订单总价，商品价格+运费
    private Double orderPrice;

    //可以使用优惠劵的金额
    private Double couponPrice;

    //配送费用
    private Double shippingPrice;

    //需要支付的金额(应付款)
    private Double needPayMoney;

    //优惠的价格总计 购物券
    private Double discountPrice = 0d;

    //红包支付 2015/10/26 humaodong
    private Double bonusPay = 0d;

    //余额
    private Double balancePay;

    //商品重量
    private Double weight;

    //可获得积分
    private Integer point;

    //满减活动详情
    private String promotionDiscountDetail;
    
    //参与满减的商品
    private String promotionDiscountItemMapDetail;
    
    //商店运费 key 商店ID value 运费
    private String storeShippingMapDetail;
    //商店优惠券 key 商店ID value 优惠券
    private String storeCouponMapDetail;

    //促销活动每个order总优惠   add by Tension  减
    private Double promotionDiscount = 0d;

    private Map<String, Object> discountItem; //使用优惠的项目

    private Integer shippingid;

    private String regionid;

    private Map<Integer, DiscountItem> promotionDiscountItemMap = new HashMap<Integer, DiscountItem>();
    private Map<Integer, Double> storeShippingMap = new HashMap<Integer, Double>();
    private Map<Integer, Double> storeCouponMap = new HashMap<Integer, Double>();

    //用户每个店铺可以参加优惠劵使用的金额，key为店铺id，value为可以参加使用优惠劵的总金额
    private Map<Integer, Double> storeUseCouponPrice = new HashMap<Integer, Double>();

    //用户每个店铺可以参加优惠劵使用的数量，key为店铺id，value为数量
    private Map<Integer, Integer> storeUseCouponCount = new HashMap<Integer, Integer>();

    public OrderPrice() {
        discountItem = new HashMap<String, Object>();

    }

    public Double getGoodsPrice() {
        if(goodsPrice != null) {
            goodsPrice = CurrencyUtil.round(goodsPrice, 2);
        } else {
            return 0.00d;
        }
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Double getOrderPrice() {
        if(orderPrice != null) {
            orderPrice = CurrencyUtil.round(orderPrice, 2);
        } else {
            return 0.00d;
        }
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    /**
     * 购物券
     * @return
     */
    public Double getDiscountPrice() {
        discountPrice = discountPrice == null ? 0 : discountPrice;
        if(discountPrice != null) {
            discountPrice = CurrencyUtil.round(discountPrice, 2);
        } else {
            return 0.00d;
        }
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    /**
     * 积分
     * @return
     */
    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Double getWeight() {
        if(weight != null) {
            weight = CurrencyUtil.round(weight, 2);
        } else {
            return 0.00d;
        }
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * 运费
     * @return
     */
    public Double getShippingPrice() {
        if(shippingPrice != null) {
            shippingPrice = CurrencyUtil.round(shippingPrice, 2);
        } else {
            return 0.00d;
        }
        return shippingPrice;
    }

    public void setShippingPrice(Double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public Map<String, Object> getDiscountItem() {
        return discountItem;
    }

    public void setDiscountItem(Map<String, Object> discountItem) {
        this.discountItem = discountItem;
    }

    public Double getNeedPayMoney() {
        if(needPayMoney != null) {
            needPayMoney = CurrencyUtil.round(needPayMoney, 2);
        } else {
            return 0.00d;
        }
        return needPayMoney;
    }

    public void setNeedPayMoney(Double needPayMoney) {
        this.needPayMoney = needPayMoney;
    }

    public Double getBonusPay() {
        return bonusPay;
    }

    public void setBonusPay(Double bonusPay) {
        this.bonusPay = bonusPay;
    }

    public Double getBalancePay() {
        if(balancePay != null) {
            balancePay = CurrencyUtil.round(balancePay, 2);
        } else {
            return 0.00d;
        }
        return balancePay;
    }

    public void setBalancePay(Double balancePay) {
        this.balancePay = balancePay;
    }

    public String getPromotionDiscountDetail() {
        return promotionDiscountDetail;
    }

    public void setPromotionDiscountDetail(String promotionDiscountDetail) {
        this.promotionDiscountDetail = promotionDiscountDetail;
    }

    /**
     * 满减
     * @return
     */
    public Double getPromotionDiscount() {
        if(promotionDiscount != null) {
            promotionDiscount = CurrencyUtil.round(promotionDiscount, 2);
        } else {
            return 0.00d;
        }
        return promotionDiscount;
    }

    public void setPromotionDiscount(Double promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public Integer getShippingid() {
        return shippingid;
    }

    public void setShippingid(Integer shippingid) {
        this.shippingid = shippingid;
    }

    public String getRegionid() {
        return regionid;
    }

    public void setRegionid(String regionid) {
        this.regionid = regionid;
    }
    
    public String getPromotionDiscountItemMapDetail() {
        return promotionDiscountItemMapDetail;
    }
    
    public void setPromotionDiscountItemMapDetail(String promotionDiscountItemMapDetail) {
        this.promotionDiscountItemMapDetail = promotionDiscountItemMapDetail;
    }
    
    public String getStoreShippingMapDetail() {
        return storeShippingMapDetail;
    }
    
    public void setStoreShippingMapDetail(String storeShippingMapDetail) {
        this.storeShippingMapDetail = storeShippingMapDetail;
    }
    
    public String getStoreCouponMapDetail() {
        return storeCouponMapDetail;
    }
    
    public void setStoreCouponMapDetail(String storeCouponMapDetail) {
        this.storeCouponMapDetail = storeCouponMapDetail;
    }
    
    public Double getGoodsBasePrice() {
        return goodsBasePrice;
    }
    
    public void setGoodsBasePrice(Double goodsBasePrice) {
        this.goodsBasePrice = goodsBasePrice;
    }

    public Map<Integer, DiscountItem> getPromotionDiscountItemMap() {
        return promotionDiscountItemMap;
    }
    
    public void setPromotionDiscountItemMap(Map<Integer, DiscountItem> promotionDiscountItemMap) {
        this.promotionDiscountItemMap = promotionDiscountItemMap;
    }

    public Map<Integer, Double> getStoreShippingMap() {
        return storeShippingMap;
    }

    public void setStoreShippingMap(Map<Integer, Double> storeShippingMap) {
        this.storeShippingMap = storeShippingMap;
    }

    public Map<Integer, Double> getStoreCouponMap() {
        return storeCouponMap;
    }

    public void setStoreCouponMap(Map<Integer, Double> storeCouponMap) {
        this.storeCouponMap = storeCouponMap;
    }

    public boolean isDiscountValid() {
        return (goodsPrice + shippingPrice) > discountPrice;
    }
    
    public boolean isBonusValid() {
        double delta = goodsPrice + shippingPrice - discountPrice;  // - promotionDiscount;
        return bonusPay < delta;
    }

    public void setCouponPrice(Double couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Double getCouponPrice() {
        return couponPrice;
    }

    public void setStoreUseCouponPrice(Map<Integer, Double> storeUseCouponPrice) {
        this.storeUseCouponPrice = storeUseCouponPrice;
    }

    public Map<Integer, Double> getStoreUseCouponPrice() {
        return storeUseCouponPrice;
    }

    public void setStoreUseCouponCount(Map<Integer, Integer> storeUseCouponCount) {
        this.storeUseCouponCount = storeUseCouponCount;
    }

    public Map<Integer, Integer> getStoreUseCouponCount() {
        return storeUseCouponCount;
    }
}
