package com.enation.app.shop.core.model;

import java.util.Map;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.StringUtil;

/**
 * 
 * @author kingapex
 *2010-4-6下午04:11:42
 */
public class OrderItem  implements java.io.Serializable {

	 
	
	private static final long serialVersionUID = 8531790664258169824L;
	
	
	private Integer item_id;
	private Integer order_id;
	private Integer goods_id;
	private Integer product_id;
	private Integer num;
	private Integer ship_num;
	private String name;
	private String sn;
	private String image;
	private Integer store; //对应货品的库存
	private String addon;
	private Integer cat_id; // 此商品的分类
	private Double price = 0D;
	private int gainedpoint;
	private int state;//订单货物状态
	private String change_goods_name;//换到的货物名称
	private Integer change_goods_id;//换到的货物ID
	
	private String unit;
    private String depotId; //库房Id 2014-6-8 李奋龙
	
	//add by lxl 
    private Integer market_enable;
    private Integer disable;
    private String other;
    
    //满减分摊
    private Double promotion_proportion = 0D;
    //运费分摊
    private Double freight_proportion = 0D;
    //红包分摊
    private Double red_packets_proportion = 0D;
    //优惠券分摊
    private Double coupon_proportion = 0D;
    //佣金
    private Double commission = 0D;

    private Double commission_rate;
    
    /**
     * 满减分摊
     * @return
     */
    public Double getPromotion_proportion() {
        return promotion_proportion;
    }
    
    public void setPromotion_proportion(Double promotion_proportion) {
        this.promotion_proportion = promotion_proportion;
    }
    
    /**
     * 运费分摊
     * @return
     */
    public Double getFreight_proportion() {
        return freight_proportion;
    }
    
    public void setFreight_proportion(Double freight_proportion) {
        this.freight_proportion = freight_proportion;
    }

    /**
     * 红包分摊
     * @return
     */
    public Double getRed_packets_proportion() {
        return red_packets_proportion;
    }

    public void setRed_packets_proportion(Double red_packets_proportion) {
        this.red_packets_proportion = red_packets_proportion;
    }
    
    /**
     * 优惠券分摊
     * @return
     */
    public Double getCoupon_proportion() {
        return coupon_proportion;
    }
    
    public void setCoupon_proportion(Double coupon_proportion) {
        this.coupon_proportion = coupon_proportion;
    }

    /**
     * 佣金
     * @return
     */
    public Double getCommission() {
        return commission;
    }
    
    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public int getState() {
	   return state;
    }

    public void setState(int state) {
	   this.state = state;
    }

    public String getChange_goods_name() {
	   return change_goods_name;
    }

    public void setChange_goods_name(String change_goods_name) {
	   this.change_goods_name = change_goods_name;
    }

    public Integer getChange_goods_id() {
	   return change_goods_id;
    }

    public void setChange_goods_id(Integer change_goods_id) {
	   this.change_goods_id = change_goods_id;
    }

    public Integer getGoods_id() {
	   return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
	   this.goods_id = goods_id;
    }

    @PrimaryKeyField
    public Integer getItem_id() {
	   return item_id;
    }

    public void setItem_id(Integer item_id) {
	   this.item_id = item_id;
    }

    public String getName() {
	   return name;
    }

    public void setName(String name) {
	   this.name = name;
    }

    public Integer getNum() {
	   return num;
    }

    public void setNum(Integer num) {
	   this.num = num;
    }

    public Integer getOrder_id() {
	   return order_id;
    }

    public void setOrder_id(Integer order_id) {
	   this.order_id = order_id;
    }

    public Integer getShip_num() {
	   return ship_num;
    }

    public void setShip_num(Integer ship_num) {
	   this.ship_num = ship_num;
    }

    public Double getPrice() {
	   return price;
    }

    public void setPrice(Double price) {
	   this.price = price;
    }

    public int getGainedpoint() {
	   return gainedpoint;
    }

    public void setGainedpoint(int gainedpoint) {
	   this.gainedpoint = gainedpoint;
    }

    public String getSn() {
	   return sn;
    }

    public void setSn(String sn) {
	   this.sn = sn;
    }

    @NotDbField
    public Integer getStore() {
	   return store;
    }

    public void setStore(Integer store) {
	   this.store = store;
    }

    public String getAddon() {
	   return addon;
    }

    public void setAddon(String addon) {
	   this.addon = addon;
    }

    public int getCat_id() {
	   return cat_id;
    }

    public void setCat_id(int cat_id) {
	   this.cat_id = cat_id;
    }

    @NotDbField
    public String getOther() {
	   return other;
    }

    public void setOther(String other) {
	   this.other = other;
    }

    public String getImage() {

	   if(!StringUtil.isEmpty(image)) {
		  image = UploadUtil.replacePath(image);
	   }

	   return image;
    }

    public void setImage(String image) {
	   this.image = image;
    }

    public Integer getProduct_id() {
	   return product_id;
    }

    public void setProduct_id(Integer product_id) {
	   this.product_id = product_id;
    }

    public String getUnit() {
	   return unit;
    }

    public void setUnit(String unit) {
	   this.unit = unit;
    }

    public String getDepotId() {
	   return depotId;
    }

    public void setDepotId(String depotId) {
	   this.depotId = depotId;
    }

    public Integer getMarket_enable() {
	   return market_enable;
    }

    public void setMarket_enable(Integer market_enable) {
	   this.market_enable = market_enable;
    }

    public Integer getDisable() {
	   return disable;
    }

    public void setDisable(Integer disable) {
	   this.disable = disable;
    }

    public Double getCommission_rate() {
        return commission_rate;
    }

    public void setCommission_rate(Double commission_rate) {
        this.commission_rate = commission_rate;
    }

    @NotDbField
    public Double getSubtotal(){
        Double subtotal = 0D;
        if(price == null) {
            price = 0D;
        }

        if(coupon_proportion == null) {
            coupon_proportion = 0D;
        }

        if(promotion_proportion == null) {
            promotion_proportion = 0D;
        }
        try {
            subtotal = num * price  - coupon_proportion - promotion_proportion;
        }  catch (Exception e) {
            subtotal = 0D;
        }

        return subtotal;
    }
}