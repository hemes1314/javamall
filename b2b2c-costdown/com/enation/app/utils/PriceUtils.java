package com.enation.app.utils;

import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.groupbuy.core.model.GroupBuy;


/**
 * 价格比较 
 * @author Jeffrey
 *
 */
public class PriceUtils {

    /**
     * 获取价格最低的 活动
     * @param ps
     * @return
     */
    public static IActivity getMinimumPriceActivity(IActivity ...ps) {
        IActivity a = null;
        for (IActivity p : ps) {
            if (null == p)
                continue;
            if (a == null) {
                a = p;
                continue;
            }
            if (null != a && a.getPrice() < p.getPrice())
                continue;
            if (a.getPrice() == p.getPrice() && a.getPriority() >= p.getPriority())
                continue;
            a = p;
        }
        return a;
    }
    
    /**
     * 活动 价格
     * @author Jeffrey
     *
     */
    public static interface IActivity {
        
        public double getPrice();
        
        /**
         * 优先级 预售 > 闪购 > 直降
         * @return
         */
        public int getPriority();
        
    }
    
    public static void main(String[] args) {
//        GroupBuy b = new GroupBuy();
//        b.setPrice(10d);
//        AdvBuy a = new AdvBuy();
//        a.setPrice(10d);
//        CostDown c = new CostDown();
//        c.setPrice(10d);
//        IActivity i = getMinimumPriceActivity(a, b, c);
//        System.out.println(i.getPriority() + " - " + i.toString());
        System.out.println(System.currentTimeMillis());
    }
    
}
