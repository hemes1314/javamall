package com.enation.app.b2b2c.core.model.Drools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enation.app.shop.core.model.Activity;
import com.enation.app.shop.core.model.Activity.PromotionRule;
import com.enation.framework.util.CurrencyUtil;

public class PromoActivity implements Serializable {

    private static final long serialVersionUID = 5443272705707400049L;

    private String id;
    private String gift_id;
    private String promotionRule;

    /**
     * 本次购买 参与活动的商品
     */
    private List<DiscountItem> items = new ArrayList<DiscountItem>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getPromotionRule() {
        return promotionRule;
    }

    public void setPromotionRule(String promotionRule) {
        this.promotionRule = promotionRule;
    }

    /**
     * 获取活动商品的满减优惠
     * @return
     */
    public double getDiscount() {
        double price = 0d;
        for (DiscountItem item : items)
            price = CurrencyUtil.add(price, (CurrencyUtil.mul(item.getPrice(), item.getNum())));
        List<PromotionRule> rules = Activity.getPromotionRulesByStr(promotionRule);
        Collections.reverse(rules);
        for (PromotionRule rule : rules) {
            if (rule.getD1() > price)
                continue;
            int i = 0;
            //其他商品分摊
            double otherProportion = 0d;
            //做分摊计算
            for (DiscountItem item : items) {
                item.setActivityDiscountPrice(rule.getD2());
                //最后一个 特殊处理
                if (++i == items.size()) {
                    if (rule.getD2() > otherProportion)
                        item.setProportion(CurrencyUtil.sub(rule.getD2(), otherProportion));
                    else
                        item.setProportion(0d);
                    continue;
                }
                //四舍五入 到1位小数
                item.setProportion(Math.round(item.getPrice() * item.getNum() / price * rule.getD2() * 10) / 10d);
                otherProportion = CurrencyUtil.add(item.getProportion(), otherProportion);
            }
            return rule.getD2();
        }
        return 0d;
    }
    
    public List<DiscountItem> getItems() {
        return items;
    }

    public void addItem(IDiscountItem item) {
        items.add(new DiscountItem(item));
    }
    
    /**
     * 涉及满减的商品
     * @author Jeffrey
     *
     */
    public static class DiscountItem {
        
        public DiscountItem() {
        }
        
        public DiscountItem(IDiscountItem item) {
            this.id = item.getId();
            this.price = item.getPrice();
            this.num = item.getNum();
        }
        
        private Integer id;
        private double price = 0d;
        private int num;
        //分摊
        private double proportion = 0d;
        //活动优惠价格 用于做分摊计算
        private double activityDiscountPrice = 0d;
        
        public Integer getId() {
            return id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public int getNum() {
            return num;
        }
        
        public void setNum(int num) {
            this.num = num;
        }

        /**
         * 活动优惠价格 用于做分摊计算
         * @return
         */
        public double getActivityDiscountPrice() {
            return activityDiscountPrice;
        }

        public void setActivityDiscountPrice(double activityDiscountPrice) {
            this.activityDiscountPrice = activityDiscountPrice;
        }

        /**
         * 获取分摊
         * @return
         */
        public double getProportion() {
            return proportion;
        }

        public void setProportion(double proportion) {
            this.proportion = proportion;
        }
        
    }
    
    public static interface IDiscountItem {
        
        public Integer getId();
        public Double getPrice();
        public int getNum();
        
    }

}
