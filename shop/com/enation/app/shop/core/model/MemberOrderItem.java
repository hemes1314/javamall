package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class MemberOrderItem {

	private int id;
	private Long member_id;
	private Integer goods_id;
	private Integer order_id;
	private Integer item_id;
	private Integer commented;
	private Long comment_time;
	
	public MemberOrderItem() {
		super();
	}
	
	public MemberOrderItem(int id, Long member_id, Integer goods_id, Integer order_id,
			Integer item_id, Integer commented, Long comment_time) {
		super();
		this.id = id;
		this.member_id = member_id;
		this.goods_id = goods_id;
		this.order_id = order_id;
		this.item_id = item_id;
		this.commented = commented;
		this.comment_time = comment_time;
	}
	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Long getMember_id() {
		return member_id;
	}
	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public Integer getItem_id() {
		return item_id;
	}
	public void setItem_id(Integer item_id) {
		this.item_id = item_id;
	}
	public Integer getCommented() {
		return commented;
	}
	public void setCommented(Integer commented) {
		this.commented = commented;
	}
	public Long getComment_time() {
		return comment_time;
	}
	public void setComment_time(Long comment_time) {
		this.comment_time = comment_time;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	
	
	
}
