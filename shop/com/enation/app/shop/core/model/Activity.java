package com.enation.app.shop.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class Activity implements java.io.Serializable {

    private static final long serialVersionUID = -6056498875779252117L;

    private Integer id;
    private String name;
    private Long start_time;
    private Long end_time;
    private Integer is_enable;
    private String description;

    //购买规则 字符串
    private String promotion_rule;
    private Integer goods_id;
    
    @PrimaryKeyField
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public Integer getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(Integer is_enable) {
        this.is_enable = is_enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }

    public String getPromotion_rule() {
        return promotion_rule;
    }
    
    public void setPromotion_rule(String promotion_rule) {
        this.promotion_rule = promotion_rule;
    }

    @NotDbField
    public void setPromotionRules(String[] strs) {
        if (strs.length == 0 || strs.length % 2 != 0)
            return;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String str : strs) {
            try {
                sb.append(Double.valueOf(str.trim()));
                if (strs.length != ++i) {
                    if (i %  2 == 0) {
                        sb.append(",");
                    } else {
                        sb.append(":");
                    }
                }
            } catch (Exception e) {
                return;
            }
        }
        promotion_rule = sb.toString();
    }
    
    /**
     * 规则字符串 转成 对应规则对象
     * @return
     */
    @NotDbField
    public List<PromotionRule> getPromotionRules() {
        return getPromotionRulesByStr(promotion_rule);
    }
    
    /**
     * 规则字符串 转成 对应规则对象
     * @return
     */
    public static List<PromotionRule> getPromotionRulesByStr(String promotionRule) {
        List<PromotionRule> list = new ArrayList<PromotionRule>();
        if (!StringUtils.isBlank(promotionRule)) {
            String[] strs1 = promotionRule.split(",");
            String[] strs2 = null;
            for (String str : strs1) {
                strs2 = str.split(":");
                if (strs2.length != 2)
                    continue;
                try {
                    list.add((new PromotionRule(Double.valueOf(strs2[0].trim()), Double.valueOf(strs2[1].trim()))));
                } catch (Exception e) {}
            }
            
        }
        Collections.sort(list);
        return list;
    }
    
    /**
     * 购买规则
     * @author Jeffrey
     *
     */
    public static class PromotionRule implements Comparable<PromotionRule> {
        
        public PromotionRule(Double d1, Double d2) {
            this.d1 = d1;
            this.d2 = d2;
        }
        
        //满多少
        private Double d1;
        //减多少
        private Double d2;
        
        public Double getD1() {
            return d1;
        }
        
        public void setD1(Double d1) {
            this.d1 = d1;
        }
        
        public Double getD2() {
            return d2;
        }
        
        public void setD2(Double d2) {
            this.d2 = d2;
        }

        @Override
        public int compareTo(PromotionRule o) {
            return d2.compareTo(o.d2);
        }
        
        @Override
        public String toString() {
            return d1 + ":" + d2;
        }
        
    }
    
    
    
    
    //test
    public static void main(String[] args) {
        Activity a = new Activity();
        a.setPromotion_rule("10:2,11:3,89:1,14:10,8:10");
        for(PromotionRule p : a.getPromotionRules()) {
            System.out.println(p.toString());
        };
    }

}