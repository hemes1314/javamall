package com.enation.app.b2b2c.core.model.bill;

import com.enation.framework.database.NotDbField;

/**
 * 结算单详细
 * @author fenlongli
 *
 */
public class BillDetail {
	
	private Integer id;		//详细单ID
	private String sn;		//详细单编号
	private Integer bill_id;	//结算单Id
	private Integer status;	//结算单状态
	
	private Long start_time;	//开始时间
	private Long end_time;		//结束时间
	private Long bill_time;		//出账日期
	
	private Integer store_id;	//店铺Id
	private String store_name;	//店铺名称
	
	private Double price; 	//订单总金额
	private Double bill_price;		//结算金额
	private Double returned_price;	//退货金额
	private Double commi_price;		//佣金金额
	private Double returned_commi_price = 0D;	//退还佣金
	private Double red_packets_price; //红包金额
	private Double returned_red_packets_price;	//总退还红包



	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getBill_id() {
		return bill_id;
	}
	public void setBill_id(Integer bill_id) {
		this.bill_id = bill_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public Long getBill_time() {
		return bill_time;
	}
	public void setBill_time(Long bill_time) {
		this.bill_time = bill_time;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getReturned_price() {
		return returned_price;
	}
	public void setReturned_price(Double returned_price) {
		this.returned_price = returned_price;
	}
	public Double getCommi_price() {
		return commi_price;
	}
	public void setCommi_price(Double commi_price) {
		this.commi_price = commi_price;
	}
	public Double getBill_price() {
		return bill_price;
	}
	public void setBill_price(Double bill_price) {
		this.bill_price = bill_price;
	}
	public Double getReturned_commi_price() {
		return returned_commi_price;
	}
	public void setReturned_commi_price(Double returned_commi_price) {
		this.returned_commi_price = returned_commi_price;
	}


	@NotDbField
	public Double getRed_packets_price() {return red_packets_price;}
	public void setRed_packets_price(Double red_packets_price) {this.red_packets_price = red_packets_price;}

	@NotDbField
	public Double getReturned_red_packets_price() {return returned_red_packets_price;}
	public void setReturned_red_packets_price(Double returned_red_packets_price) {this.returned_red_packets_price = returned_red_packets_price;}

}
