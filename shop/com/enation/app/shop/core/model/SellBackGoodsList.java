package com.enation.app.shop.core.model;


/**
 * 退货商品表
 * @author lina
 *2013-11-10上午10:19:58
 */
public class SellBackGoodsList {
	
	private Integer id;
	private Integer recid;//退货单id
	private Integer goods_id;
	private Integer ship_num;//发货数量
	private Double price;//销售价格
	private Integer return_num;//退货数量
	private Integer storage_num;//入库数量
	private String goods_remark;//退货商品备注
	private Integer product_id;
	private Integer return_type;//退货类型   0全部退货 1部分退货
	 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
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
	public Integer getReturn_num() {
		return return_num;
	}
	public void setReturn_num(Integer return_num) {
		this.return_num = return_num;
	}
	public Integer getStorage_num() {
		return storage_num;
	}
	public void setStorage_num(Integer storage_num) {
		this.storage_num = storage_num;
	}
	public String getGoods_remark() {
		return goods_remark;
	}
	public void setGoods_remark(String goods_remark) {
		this.goods_remark = goods_remark;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	public Integer getReturn_type() {
		return return_type;
	}
	public void setReturn_type(Integer return_type) {
		this.return_type = return_type;
	}
	
 
	
}
