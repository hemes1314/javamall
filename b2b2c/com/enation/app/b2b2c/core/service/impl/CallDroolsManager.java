package com.enation.app.b2b2c.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.enation.app.b2b2c.core.model.Drools.OrderProductsActivity;
import com.enation.app.b2b2c.core.model.Drools.Product;
import com.enation.app.b2b2c.core.model.Drools.PromoActivity;
import com.enation.app.b2b2c.core.model.Drools.PromoActivity.DiscountItem;
import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.shop.core.model.Activity;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.impl.ActivityGiftManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.app.shop.core.service.impl.ActivityManager;
import com.enation.framework.util.CurrencyUtil;

@Component
public class CallDroolsManager {
    
    @Value("#{configProperties['address.droolsRestUrl']}")
    private String droolsRestUrl;
    //private final String droolsRestUrl = "http://192.168.0.215:7108/drools-activiti/restful/rules/executeOrderRules";
    
    ActivityGoodsManager activityGoodsManager;
    ActivityManager activityManager;
    ActivityGiftManager activityGiftManager;
    
    
    /**
     * 需要 getGoodsPrice getShippingPrice
     * @param lists
     * @param orderPrice
     */
    public void getRestfulResult(List<StoreCartItem> lists, OrderPrice orderPrice) {
        List<Product> productList = new ArrayList<Product>();
        Map<Integer, Activity> activityMap = new HashMap<Integer, Activity>();
        Map<Integer, PromoActivity> promoActivityMap = new HashMap<Integer, PromoActivity>();
        OrderProductsActivity orderProductsActivity = new OrderProductsActivity();

        //可以使用优惠劵的总金额
        double couponPrice = 0L;
        //用户每个店铺可以参加优惠劵使用的金额，key为店铺id，value为可以参加使用优惠劵的总金额
        Map<Integer, Double> storeUseCouponPrice = new HashMap<Integer, Double>();

        //用户每个店铺可以参加优惠劵使用的数量，key为店铺id，value为数量
        Map<Integer, Integer> storeUseCouponCount = new HashMap<Integer, Integer>();

        for (StoreCartItem storeCartItem: lists) {
            Product product = new Product();
            product.setId(storeCartItem.getGoods_id().toString());
            product.setQuantity(storeCartItem.getNum());
            product.setPrice(storeCartItem.getPrice());
            Map<?, ?> activityGoodsMap = activityGoodsManager.getActivityByGoods(storeCartItem.getGoods_id());
            Integer activityId  = activityGoodsMap == null ? null : Integer.valueOf(activityGoodsMap.get("activity_id").toString());
            
            if (activityId != null) {
                product.setPromoId(activityId.toString());
                if ((activityGoodsMap != null)) {
                    Activity activity = null;
                    PromoActivity promoActivity = null;
                    if (null == (activity = activityMap.get(activityId))) {
                        activity = activityManager.get(activityId);
                        promoActivity = new PromoActivity();
                        promoActivity.setId(activityId.toString());
                        promoActivity.setPromotionRule(activity.getPromotion_rule());
                        activityMap.put(activityId.intValue(), activity);
                        promoActivityMap.put(activityId, promoActivity);
                        orderProductsActivity.getActivits().add(promoActivity);
                    } else
                        promoActivity = promoActivityMap.get(activityId);
                    //闪购，预售不参与满减 
                    if (storeCartItem.getCurrent_activity() != Cart.TYPE_GROUPBUY 
                            && storeCartItem.getCurrent_activity() != Cart.TYPE_AVDBUY)
                        promoActivity.addItem(storeCartItem);
                    
                    //取消礼品
//                String activityGifts = activityGiftManager.getByActivityId(activityId);
//                
//                if (activityGifts != null) {
//                    promoActivity.setGift_id(activityGifts);
//                }
                }
            } else {
                //计算每个店铺可以使用优惠劵总金额
                if (storeUseCouponPrice.containsKey(storeCartItem.getStore_id())) {
                    storeUseCouponPrice.put(storeCartItem.getStore_id(), CurrencyUtil.add(storeUseCouponPrice.get(storeCartItem.getStore_id()), CurrencyUtil.mul(storeCartItem.getNum(), storeCartItem.getPrice())));
                } else {
                    storeUseCouponPrice.put(storeCartItem.getStore_id(), CurrencyUtil.mul(storeCartItem.getNum(), storeCartItem.getPrice()));
                }

                //计算每个店铺可以使用优惠劵总数量
                if (storeUseCouponCount.containsKey(storeCartItem.getStore_id())) {
                    storeUseCouponCount.put(storeCartItem.getStore_id(), storeUseCouponCount.get(storeCartItem.getStore_id()) + 1);
                } else {
                    storeUseCouponCount.put(storeCartItem.getStore_id(), 1);
                }
                couponPrice = CurrencyUtil.add(couponPrice, CurrencyUtil.mul(storeCartItem.getNum(), storeCartItem.getPrice()));
            }
            productList.add(product);
        }
        orderPrice.setStoreUseCouponPrice(storeUseCouponPrice);
        orderPrice.setStoreUseCouponCount(storeUseCouponCount);
        
        orderProductsActivity.setList(productList);

        orderPrice.setCouponPrice(couponPrice);
        
        Map<Integer, DiscountItem> discountItemMap = new HashMap<Integer, DiscountItem>();
        double discount = 0d;
        for (PromoActivity activity : orderProductsActivity.getActivits()) {
            double aDiscount = activity.getDiscount();
            if (aDiscount == 0d)
                continue;
            discount = CurrencyUtil.add(discount, aDiscount);
            //涉及满减活动的商品 冗余 用于做分摊计算
            for (DiscountItem item : activity.getItems())
                discountItemMap.put(item.getId(), item);
        }

        //获取店铺金额（商品数量*商品单价+运费）
        if(orderPrice.getGoodsBasePrice() != null && orderPrice.getShippingPrice() != null)
        {
            orderPrice.setOrderPrice(CurrencyUtil.add(orderPrice.getGoodsBasePrice(),orderPrice.getShippingPrice()));
        }

        //满减
        orderPrice.setPromotionDiscount(discount);
        orderPrice.setPromotionDiscountDetail(JSON.toJSONString(orderProductsActivity.getActivits()));
        orderPrice.setPromotionDiscountItemMapDetail(JSON.toJSONString(discountItemMap));
        orderPrice.setPromotionDiscountItemMap(discountItemMap);
        //优惠后的价格
        orderPrice.setGoodsPrice(CurrencyUtil.add(orderPrice.getGoodsBasePrice(), -orderPrice.getPromotionDiscount()));
        
        //订单总金额:商品金额+运费-促销
        Double orderTotal = CurrencyUtil.add(orderPrice.getGoodsPrice(), orderPrice.getShippingPrice());
//        orderTotal = CurrencyUtil.add(orderTotal, -orderPrice.getPromotionDiscount()); //2015/10/28 humaodong
        
        
        //应付金额为订单总金额
        orderPrice.setNeedPayMoney(orderTotal); 
        
        //如果优惠金额后订单价格小于0
        if (orderPrice.getNeedPayMoney() <= 0)
            orderPrice.setNeedPayMoney(0d);
        
//        价格规则计算
//        HttpPost httpPost = new HttpPost(droolsRestUrl);
//        String returnData = null;
//        try {
//            StringEntity entity = new StringEntity(postValue);
//            entity.setContentEncoding("UTF-8");
//            entity.setContentType("application/json");
//            httpPost.setEntity(entity);
//            HttpClient client = new DefaultHttpClient();
//            HttpResponse response = client.execute(httpPost);
//            
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
//                // 解析数据  
//                returnData = EntityUtils.toString(response.getEntity());  
//            }
//        } catch(Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        //System.out.println(returnData);
//        //DroolsReturnModel droolsReturnModel = JSON.parseObject("{\"code\":\"0\",\"data\":{\"promotioningIds\":[\"2\"],\"gifts\":[],\"price\":17.6},\"msg\":\"成功！\"} ", DroolsReturnModel.class);
//        DroolsReturnModel droolsReturnModel = JSON.parseObject(returnData, DroolsReturnModel.class);
//        
//        if (droolsReturnModel.getCode().equals("0")) {
//            Map<String, Object> droolsReturnMap = JSON.parseObject(droolsReturnModel.getData().toString(), new TypeReference<Map<String, Object>>(){});
//            DroolsReturnData droolsReturnData = new DroolsReturnData();
//            droolsReturnData.setActivities_promotions((Map<String, List<Double>>)droolsReturnMap.get("activities_promotions"));
//            droolsReturnData.setGifts((List<String>)droolsReturnMap.get("gifts"));
//            droolsReturnData.setPrice(NumberUtils.toDouble(droolsReturnMap.get("price").toString()));
//            droolsReturnData.setFree_shipping(NumberUtils.toInt(droolsReturnMap.get("free_shipping").toString()));
//            return droolsReturnData;
//        } else {
//            return null;
//        }
        
       // return 0D;
    }
    
    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }
    
    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }
    
    public ActivityManager getActivityManager() {
        return activityManager;
    }
    
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
    
    public ActivityGiftManager getActivityGiftManager() {
        return activityGiftManager;
    }
    
    public void setActivityGiftManager(ActivityGiftManager activityGiftManager) {
        this.activityGiftManager = activityGiftManager;
    }
 
}