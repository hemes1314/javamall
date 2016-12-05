package com.enation.app.shop.core.model.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.Drools.PromoActivity.IDiscountItem;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Promotion;
import com.enation.framework.database.NotDbField;
import com.enation.framework.util.CurrencyUtil;

/**
 * 购物项
 * 
 * @author kingapex 2010-5-5上午10:13:27
 */
/**
 * @author Jeffrey
 *
 */
public class CartItem implements IDiscountItem {

    private Integer Id;

    private Integer cart_id;

    private Integer product_id;

    private Integer goods_id;

    private Integer goodsType;
    private Integer goodsCatId;

    private String name;

    private Double mktprice;

    private Double price;

    private Double coupPrice; // 优惠后的价格

    private Double subtotal; // 小计

    private int num;

    private Integer limitnum; //限购数量(对于赠品)

    private String image_default;

    private Integer point;

    private Integer itemtype; //物品类型(0商品，1捆绑商品，2赠品)

    private String sn; // 捆绑促销的货号 

    private String addon; //配件字串

    private String specs;

    private int catid; //是否货到付款

    private Map others; //扩展项,可通过 ICartItemFilter 进行过滤修改

    private String ActivityName; //add by Tension 促销名称

    private String giftName; //add by Tension 促销赠送的礼物名称

    private String isCostDown; //add by Jeffrey 直降

    private String isGroupbuy; //add by Tension 闪购

    private String isFlashbuy; //add by Tension 限时抢购

    private String isSecbuy; //add by Tension 秒拍

    private String isAdvbuy; //add by Tension 预售

    private String unit;

    private String thumbnail;

    private Integer store_id;

    private String store_name;

    private Integer favorite_id;

    //此购物项所享有的优惠规则
    private List<Promotion> pmtList;

    //是否在购物车中选中了
    private Integer selected;

    private double weight;

    //当前活动 
    private int current_activity = Cart.TYPE_NO_ACTIVITY;

    //当前活动名称
    private String current_activity_name = "";

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setPmtList(List<Promotion> pmtList) {
        this.pmtList = pmtList;
    }

    @NotDbField
    public List<Promotion> getPmtList() {
        return this.pmtList;
    }

    @NotDbField
    public Map getOthers() {
        if(others == null) others = new HashMap();
        return others;
    }

    public void setOthers(Map others) {
        this.others = others;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer productId) {
        product_id = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMktprice() {
        return mktprice;
    }

    public void setMktprice(Double mktprice) {
        this.mktprice = mktprice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getCoupPrice() {
        return coupPrice;
    }

    public void setCoupPrice(Double coupPrice) {
        this.coupPrice = coupPrice;
    }

    public Double getSubtotal() {
        this.subtotal = CurrencyUtil.mul(this.num, this.coupPrice);
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getImage_default() {
        return image_default;
    }

    public void setImage_default(String imageDefault) {
        image_default = imageDefault;
    }

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goodsId) {
        goods_id = goodsId;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getLimitnum() {
        return limitnum;
    }

    public void setLimitnum(Integer limitnum) {
        this.limitnum = limitnum;
    }

    public Integer getItemtype() {
        return itemtype;
    }

    public void setItemtype(Integer itemtype) {
        this.itemtype = itemtype;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAddon() {
        return addon;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getActivityName() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        ActivityName = activityName;
    }

    public String getIsCostDown() {
        return isCostDown;
    }

    public void setIsCostDown(String isCostDown) {
        this.isCostDown = isCostDown;
    }

    public String getIsGroupbuy() {
        return isGroupbuy;
    }

    public void setIsGroupbuy(String isGroupbuy) {
        this.isGroupbuy = isGroupbuy;
    }

    public String getIsFlashbuy() {
        return isFlashbuy;
    }

    public void setIsFlashbuy(String isFlashbuy) {
        this.isFlashbuy = isFlashbuy;
    }

    public String getIsSecbuy() {
        return isSecbuy;
    }

    public void setIsSecbuy(String isSecbuy) {
        this.isSecbuy = isSecbuy;
    }

    public String getIsAdvbuy() {
        return isAdvbuy;
    }

    public void setIsAdvbuy(String isAdvbuy) {
        this.isAdvbuy = isAdvbuy;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public Integer getCart_id() {
        return cart_id;
    }

    public void setCart_id(Integer cart_id) {
        this.cart_id = cart_id;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Integer getFavorite_id() {
        return favorite_id;
    }

    public void setFavorite_id(Integer favorite_id) {
        this.favorite_id = favorite_id;
    }

    public Integer getSelected() {
        return selected == null ? 0 : selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
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

    public Integer getGoodsType() {
        return goodsType;
    }
    
    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }
    
    public Integer getGoodsCatId() {
        return goodsCatId;
    }
    
    public void setGoodsCatId(Integer goodsCatId) {
        this.goodsCatId = goodsCatId;
    }

}
