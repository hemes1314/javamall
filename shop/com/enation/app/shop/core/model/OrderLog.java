package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 订单日志
 * @author kingapex
 *2010-4-6下午05:24:16
 */
public class OrderLog {
	 	
	private Integer  log_id;		
	private Integer order_id;	
	private Long op_id;		
	private String op_name;		
	private String message;		
	private Long op_time;
	@PrimaryKeyField
	public Integer getLog_id() {
		return log_id;
	}
	public void setLog_id(Integer logId) {
		log_id = logId;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer orderId) {
		order_id = orderId;
	}
	public Long getOp_id() {
		return op_id;
	}
	public void setOp_id(Long opId) {
		op_id = opId;
	}
	public String getOp_name() {
		return op_name;
	}
	public void setOp_name(String opName) {
		op_name = opName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getOp_time() {
		return op_time;
	}
	public void setOp_time(Long opTime) {
		op_time = opTime;
	}
 
	
}
