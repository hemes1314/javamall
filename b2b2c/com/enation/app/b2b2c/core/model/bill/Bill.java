package com.enation.app.b2b2c.core.model.bill;

import com.enation.framework.database.NotDbField;

/**
 * 结算单
 * @author fenlongli
 *
 */
public class Bill {

	private Integer bill_id;			//账单ID
	private String name;		//账单名称
	private Long start_time;	//开始时间
	private Long end_time;		//结束时间
	//TODO MONSOON 结算单 总金额 不对
	private Double price;		//总金额
	
	private Double order_price;	//总订单金额
	private Double commi_price;	//总佣金
	private Double returned_price;	//总退货金额
	private Double returned_commi_price;	//总退还佣金

	private Double red_packets_price;	//总红包
	private Double returned_red_packets_price;	//总退还红包
	
	//get set

	public String getName() {
		return name;
	}
	public Integer getBill_id() {
		return bill_id;
	}
	public void setBill_id(Integer bill_id) {
		this.bill_id = bill_id;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getOrder_price() {
		return order_price;
	}
	public void setOrder_price(Double order_price) {
		this.order_price = order_price;
	}
	public Double getCommi_price() {
		return commi_price;
	}
	public void setCommi_price(Double commi_price) {
		this.commi_price = commi_price;
	}
	public Double getReturned_price() {
		return returned_price;
	}
	public void setReturned_price(Double returned_price) {
		this.returned_price = returned_price;
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
