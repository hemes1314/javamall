package com.enation.app.shop.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员积分历史记录
 * 
 * @author lzf<br/>
 *         2010-3-22 上午11:01:31<br/>
 *         version 1.0<br/>
 */
public class PointHistory  {
	
	
	private int id;
	private long member_id;
	private int point;
	private Long time;
	private String reason;
	private String cnreason;
	private Integer related_id;
	private int type;
	private String operator;
	private Integer mp; 
	private int point_type; //0为等级积分1为消费积分
	
	private Integer total_point;
	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getMember_id() {
		return member_id;
	}
	public void setMember_id(long memberId) {
		member_id = memberId;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
 
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@NotDbField
	public String getCnreason() {
		if(reason.equals("order_pay_use")) cnreason = "订单消费积分";
		if(reason.equals("order_pay_get")) cnreason = "订单获得积分";
		if(reason.equals("order_refund_use")) cnreason = "退还订单消费积分";
		if(reason.equals("order_refund_get")) cnreason = "扣掉订单所得积分";
		if(reason.equals("order_cancel_refund_consume_gift")) cnreason = "Score deduction for gifts refunded for order cancelling.";
		if(reason.equals("exchange_coupon")) cnreason = "兑换优惠券";
		if(reason.equals("operator_adjust")) cnreason = "管理员改变积分";
		if(reason.equals("consume_gift")) cnreason = "积分换赠品";
		if(reason.equals("recommend_member")) cnreason = "发表评论奖励积分";
		return cnreason;
	}
	public void setCnreason(String cnreason) {
		this.cnreason = cnreason;
	}
	public Integer getRelated_id() {
		return related_id;
	}
	public void setRelated_id(Integer relatedId) {
		related_id = relatedId;
	}
	public Integer getMp() {
		return mp;
	}
	public void setMp(Integer mp) {
		this.mp = mp;
	}
	public int getPoint_type() {
		return point_type;
	}
	public void setPoint_type(int point_type) {
		this.point_type = point_type;
	}
    
    public Integer getTotal_point() {
        return total_point;
    }
    
    public void setTotal_point(Integer total_point) {
        this.total_point = total_point;
    }
	
	
}
