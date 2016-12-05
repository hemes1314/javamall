package com.enation.app.b2b2c.core.model.goods;

import com.enation.app.shop.core.model.Goods;

/**
 * 店铺商品
 * @author LiFenLong
 *
 */
public class StoreGoods extends Goods{

	private Integer store_id;	//店铺Id
	private Integer	store_cat_id;	//店铺分类Id
	private Integer buy_num;	//购买数量
	private Integer comment_num;	//评论次数
	private Integer template_id;		//商品运费模板
	private Integer goods_transfee_charge;	//是否为买家承担运费：0,买家承担1,卖家承担
	
	//店铺名称，2015-05-20新增-by kingapex
	private String store_name;
	
	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public Integer getStore_cat_id() {
		return store_cat_id;
	}

	public void setStore_cat_id(Integer store_cat_id) {
		this.store_cat_id = store_cat_id;
	}

	public Integer getBuy_num() {
		return buy_num;
	}

	public void setBuy_num(Integer buy_num) {
		this.buy_num = buy_num;
	}

	public Integer getComment_num() {
		return comment_num;
	}

	public void setComment_num(Integer comment_num) {
		this.comment_num = comment_num;
	}

	public Integer getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(Integer template_id) {
		this.template_id = template_id;
	}

	public Integer getGoods_transfee_charge() {
		return goods_transfee_charge;
	}

	public void setGoods_transfee_charge(Integer goods_transfee_charge) {
		this.goods_transfee_charge = goods_transfee_charge;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	
	
}
