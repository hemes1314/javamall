package com.enation.app.shop.core.model;

public class Goodslist implements java.io.Serializable{
    
    private Integer goods_id;

    private String name;
    private String activity_name;
    private Double price ;
    private Integer comment_num;
    private Integer grade;
    private String activity_id;

    private Integer buy_count;
    private String thumbnail;
    private String big;
    private String small;
    private String original;
    private String brief;
    private Double discount;
    private String is_free_shipping;
    private String goods_transfee_charge;
    public Double getDiscount() {
        return discount;
    }
    public void setDiscount(Double object) {
        this.discount = object;
    }
    public String getIs_free_shipping() {
        return is_free_shipping;
    }
    public void setIs_free_shipping(String is_free_shipping) {
        this.is_free_shipping = is_free_shipping;
    }
    public Integer getGoods_id() {
        return goods_id;
    }
    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
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
    public Integer getComment_num() {
        return comment_num;
    }
    public void setComment_num(Integer comment_num) {
        this.comment_num = comment_num;
    }
    public Integer getGrade() {
        return grade;
    }
    public void setGrade(Integer grade) {
        this.grade = grade;
    }
    public Integer getBuy_count() {
        return buy_count;
    }
    public void setBuy_count(Integer buy_count) {
        this.buy_count = buy_count;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getBig() {
        return big;
    }
    public void setBig(String big) {
        this.big = big;
    }
    public String getSmall() {
        return small;
    }
    public void setSmall(String small) {
        this.small = small;
    }
    public String getOriginal() {
        return original;
    }
    public void setOriginal(String original) {
        this.original = original;
    }
    public String getBrief() {
        return brief;
    }
    public void setBrief(String brief) {
        this.brief = brief;
    }
    public String getActivity_name() {
        return activity_name;
    }
    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }
    public String getActivity_id() {
        return activity_id;
    }
    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }
    
    public String getGoods_transfee_charge() {
        return goods_transfee_charge;
    }
    
    public void setGoods_transfee_charge(String goods_transfee_charge) {
        this.goods_transfee_charge = goods_transfee_charge;
    }
    
}