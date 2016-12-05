package com.enation.app.b2b2c.core.model.store;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 店铺
 *
 * @author LiFenLong
 */
public class Store {

    private int store_id;    //店铺Id
    private String store_name;    //店铺名称
    private int store_type; //店铺分类 0自营店1第三方店铺
    private int store_provinceid;    //省
    private int store_cityid;    //市
    private int store_regionid;    //区

    private String store_province;    //省
    private String store_city;    //市
    private String store_region;    //区

    private String attr;    //详细地址
    private String zip;        //邮编
    private String tel;    //联系方式
    private int store_level;//店铺等级
    private long member_id;    //会员Id
    private String member_name;    //会员名称
    private String id_number;    //身份证号
    private String id_img;        //身份证照片
    private String license_img;//执照照片
    private int disabled;        //店铺状态
    private Long create_time;    //创建时间
    private Long end_time;        //关闭时间
    private String store_logo;    //店铺logo
    private String store_banner;//店铺横幅
    private String description;//店铺简介
    private int store_recommend;//是否推荐
    private int store_theme;    //店铺主题
    private int store_credit;    //店铺信用
    private double praise_rate;    //店铺好评率
    private double store_desccredit;    //描述相符度
    private double store_servicecredit;    //服务态度分数
    private double store_deliverycredit;    //发货速度分数
    private int store_collect;    //店铺收藏数量
    private int store_auth;    //店铺认证
    private int name_auth;    //店主认证
    private int goods_num; //店铺商品数量
    private String qq;        //店铺客服QQ

    //为了程序稳定暂时保留
    private Double commission;    //店铺 默认 佣金比例



    private Double wine_commission = 0D;//葡萄酒佣金比例
    private Double chinese_spirits_commission = 0D;//白酒佣金比例
    private Double foreign_spirits_commission = 0D;//洋酒佣金比例
    private Double beer_commission = 0D;//啤酒佣金比例
    private Double other_spirits_commission = 0D;//黄酒/养生酒佣金比例oreign
    private Double around_commission = 0D;//酒周边佣金比例

    
    

    private String bank_account_name;        //银行开户名
    private String bank_account_number;        //公司银行账号
    private String bank_name;                //开户银行支行名称
    private String bank_code;                //支行联行号
    private Integer bank_provinceid;        //开户银行所在省Id
    private Integer bank_cityid;            //开户银行所在市Id
    private Integer bank_regionid;            //开户银行所在区Id
    private String bank_province;            //开户银行所在省
    private String bank_city;                //开户银行所在市
    private String bank_region;                //开户银行所在区


    //2015-8-6 author:TALON 新增字段 小区、坐标
    private Integer community_id;                //小区Id
    private String community;                    //小区
    private Double longitude;                    //经度
    private Double latitude;                        //纬度

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getStore_province() {
        return store_province;
    }

    public String getStore_banner() {
        return store_banner;
    }

    public void setStore_banner(String store_banner) {
        this.store_banner = store_banner;
    }

    public void setStore_province(String store_province) {
        this.store_province = store_province;
    }

    public String getStore_city() {
        return store_city;
    }

    public void setStore_city(String store_city) {
        this.store_city = store_city;
    }

    public String getStore_region() {
        return store_region;
    }

    public void setStore_region(String store_region) {
        this.store_region = store_region;
    }

    @PrimaryKeyField
    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public int getStore_provinceid() {
        return store_provinceid;
    }

    public void setStore_provinceid(int store_provinceid) {
        this.store_provinceid = store_provinceid;
    }

    public int getStore_cityid() {
        return store_cityid;
    }

    public void setStore_cityid(int store_cityid) {
        this.store_cityid = store_cityid;
    }

    public int getStore_regionid() {
        return store_regionid;
    }

    public void setStore_regionid(int store_regionid) {
        this.store_regionid = store_regionid;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getStore_level() {
        return store_level;
    }

    public void setStore_level(int store_level) {
        this.store_level = store_level;
    }

    public long getMember_id() {
        return member_id;
    }

    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getId_img() {
        return id_img;
    }

    public void setId_img(String id_img) {
        this.id_img = id_img;
    }

    public String getLicense_img() {
        return license_img;
    }

    public void setLicense_img(String license_img) {
        this.license_img = license_img;
    }

    public int getDisabled() {
        return disabled;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public String getStore_logo() {
        return store_logo;
    }

    public void setStore_logo(String store_logo) {
        this.store_logo = store_logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStore_recommend() {
        return store_recommend;
    }

    public void setStore_recommend(int store_recommend) {
        this.store_recommend = store_recommend;
    }

    public int getStore_theme() {
        return store_theme;
    }

    public void setStore_theme(int store_theme) {
        this.store_theme = store_theme;
    }

    public int getStore_credit() {
        return store_credit;
    }

    public void setStore_credit(int store_credit) {
        this.store_credit = store_credit;
    }

    public double getPraise_rate() {
        return praise_rate;
    }

    public void setPraise_rate(double praise_rate) {
        this.praise_rate = praise_rate;
    }

    public double getStore_desccredit() {
        return store_desccredit;
    }

    public void setStore_desccredit(double store_desccredit) {
        this.store_desccredit = store_desccredit;
    }

    public double getStore_servicecredit() {
        return store_servicecredit;
    }

    public void setStore_servicecredit(double store_servicecredit) {
        this.store_servicecredit = store_servicecredit;
    }

    public double getStore_deliverycredit() {
        return store_deliverycredit;
    }

    public void setStore_deliverycredit(double store_deliverycredit) {
        this.store_deliverycredit = store_deliverycredit;
    }

    public int getStore_auth() {
        return store_auth;
    }

    public void setStore_auth(int store_auth) {
        this.store_auth = store_auth;
    }

    public int getName_auth() {
        return name_auth;
    }

    public void setName_auth(int name_auth) {
        this.name_auth = name_auth;
    }

    public int getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(int goods_num) {
        this.goods_num = goods_num;
    }

    public int getStore_collect() {
        return store_collect < 0 ? 0 : store_collect;
    }

    public void setStore_collect(int store_collect) {
        this.store_collect = store_collect;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public String getBank_account_name() {
        return bank_account_name;
    }

    public void setBank_account_name(String bank_account_name) {
        this.bank_account_name = bank_account_name;
    }

    public String getBank_account_number() {
        return bank_account_number;
    }

    public void setBank_account_number(String bank_account_number) {
        this.bank_account_number = bank_account_number;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public Integer getBank_provinceid() {
        return bank_provinceid;
    }

    public void setBank_provinceid(Integer bank_provinceid) {
        this.bank_provinceid = bank_provinceid;
    }

    public Integer getBank_cityid() {
        return bank_cityid;
    }

    public void setBank_cityid(Integer bank_cityid) {
        this.bank_cityid = bank_cityid;
    }

    public Integer getBank_regionid() {
        return bank_regionid;
    }

    public void setBank_regionid(Integer bank_regionid) {
        this.bank_regionid = bank_regionid;
    }

    public String getBank_province() {
        return bank_province;
    }

    public void setBank_province(String bank_province) {
        this.bank_province = bank_province;
    }

    public String getBank_city() {
        return bank_city;
    }

    public void setBank_city(String bank_city) {
        this.bank_city = bank_city;
    }

    public String getBank_region() {
        return bank_region;
    }

    public void setBank_region(String bank_region) {
        this.bank_region = bank_region;
    }

    public Integer getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(Integer community_id) {
        this.community_id = community_id;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getWine_commission() {
        return wine_commission;
    }

    public void setWine_commission(Double wine_commission) {
        this.wine_commission = wine_commission;
    }

    public Double getChinese_spirits_commission() {
        return chinese_spirits_commission;
    }

    public void setChinese_spirits_commission(Double chinese_spirits_commission) {
        this.chinese_spirits_commission = chinese_spirits_commission;
    }

    public Double getForeign_spirits_commission() {
        return foreign_spirits_commission;
    }

    public void setForeign_spirits_commission(Double foreign_spirits_commission) {
        this.foreign_spirits_commission = foreign_spirits_commission;
    }

    public Double getBeer_commission() {
        return beer_commission;
    }

    public void setBeer_commission(Double beer_commission) {
        this.beer_commission = beer_commission;
    }

    public Double getOther_spirits_commission() {
        return other_spirits_commission;
    }

    public void setOther_spirits_commission(Double other_spirits_commission) {
        this.other_spirits_commission = other_spirits_commission;
    }

    public Double getAround_commission() {
        return around_commission;
    }
    
    public void setAround_commission(Double around_commission) {
        this.around_commission = around_commission;
    }
    
    public int getStore_type() {
        return store_type;
    }

    public void setStore_type(int store_type) {
        this.store_type = store_type;
    }
}
