package com.enation.app.flashbuy.core.model;

import com.enation.app.shop.core.model.Goods;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 团购
 *  @author kingapex
 *2015-1-2下午7:44:58
 * @author fenlongli
 *2015-3-29
 */
public class FlashBuy {

	private int gb_id;		//团购商品Id
	private int act_id;		//活动Id
	private int area_id;	//地区Id
	private String gb_name;	//团购名称
	private String gb_title;	//副标题
	private String goods_name;	//商品名称
	private int goods_id;	//商品Id
	private int product_id;	//货品Id
	private double price;	//团购价格
	private String img_url;	//图片地址
	private int goods_num;	//商品总数
	private int cat_id;		//团购分类Id
	private int visual_num;	//虚拟数量
	private int limit_num;	//限购数量
	private int buy_num;	//已团购数量
	private String remark;	//介绍
	private int gb_status;	//状态  
	private int view_num;	//浏览数量
	private long add_time;	//添加时间
	private double original_price;	//原始价格
	
	
	/**
	 * 团购对应的商品
	 * 非数据库字段
	 */
	private Goods goods;
	
	@PrimaryKeyField
	public int getGb_id() {
		return gb_id;
	}
	public void setGb_id(int gb_id) {
		this.gb_id = gb_id;
	}
	public int getAct_id() {
		return act_id;
	}
	public void setAct_id(int act_id) {
		this.act_id = act_id;
	}
	public String getGb_name() {
		return gb_name;
	}
	public void setGb_name(String gb_name) {
		this.gb_name = gb_name;
	}
	public String getGb_title() {
		return gb_title;
	}
	public void setGb_title(String gb_title) {
		this.gb_title = gb_title;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public int getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(int goods_num) {
		this.goods_num = goods_num;
	}
	public int getCat_id() {
		return cat_id;
	}
	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}
	public int getVisual_num() {
		return visual_num;
	}
	public void setVisual_num(int visual_num) {
		this.visual_num = visual_num;
	}
	public int getLimit_num() {
		return limit_num;
	}
	public void setLimit_num(int limit_num) {
		this.limit_num = limit_num;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getGb_status() {
		return gb_status;
	}
	public void setGb_status(int gb_status) {
		this.gb_status = gb_status;
	}
 
	public int getArea_id() {
		return area_id;
	}
	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}
	public long getAdd_time() {
		return add_time;
	}
	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}
	public int getBuy_num() {
		return buy_num;
	}
	public void setBuy_num(int buy_num) {
		this.buy_num = buy_num;
	}
	
	@NotDbField
	public Goods getGoods() {
		return goods;
	}
	
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getView_num() {
		return view_num;
	}
	public void setView_num(int view_num) {
		this.view_num = view_num;
	}
	public double getOriginal_price() {
		return original_price;
	}
	public void setOriginal_price(double original_price) {
		this.original_price = original_price;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	
}
