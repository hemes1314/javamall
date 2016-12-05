package com.enation.app.b2b2c.core.model.order;

import java.util.List;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.framework.database.NotDbField;

/**
 * 订单物流
 * @author linyang
 *
 */
public class OrderLogi {
	private Integer id;
	private String  key_id;
	private String  logi_num;
	private String  message;
	private String comcontact;	
	private String com;	
	private String condition;
	private String status;
	private String state;
	private String detail;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getComcontact() {
        return comcontact;
    }
    
    public void setComcontact(String comcontact) {
        this.comcontact = comcontact;
    }
    
    public String getCom() {
        return com;
    }
    
    public void setCom(String com) {
        this.com = com;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getDetail() {
        return detail;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }

    
    public String getKey_id() {
        return key_id;
    }

    
    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    
    public String getLogi_num() {
        return logi_num;
    }

    
    public void setLogi_num(String logi_num) {
        this.logi_num = logi_num;
    }
		
}
