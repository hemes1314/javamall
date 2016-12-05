package com.gomecellar.fake.model;

import java.io.Serializable;


/**
 * 用于商品造假销售量临时模型
 * @author wangli-tri
 *
 */
public class GoodsForFake implements Serializable{
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSn() {
        return sn;
    }
    
    public void setSn(String sn) {
        this.sn = sn;
    }
    
    public Integer getGoods_id() {
        return goods_id;
    }
    
    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }
    
    public Integer getGoods_fake_salesNumber() {
        return goods_fake_salesNumber;
    }
    
    public void setGoods_fake_salesNumber(Integer goods_fake_salesNumber) {
        this.goods_fake_salesNumber = goods_fake_salesNumber;
    }
    
    public Integer getGoods_fake_commentsNumber() {
        return goods_fake_commentsNumber;
    }
    
    public void setGoods_fake_commentsNumber(Integer goods_fake_commentsNumber) {
        this.goods_fake_commentsNumber = goods_fake_commentsNumber;
    }
    public Integer getCat_id() {
        return cat_id;
    }

    public void setCat_id(Integer cat_id) {
        this.cat_id = cat_id;
    }
    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }
    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }
    private static final long serialVersionUID = 1L;
    private String name;
    private String cat_name;
    private String sn;
    private Integer goods_id;
    private Integer goods_fake_salesNumber;
    private Integer goods_fake_commentsNumber;
    private Integer cat_id; 
    private Integer store_id;

}
