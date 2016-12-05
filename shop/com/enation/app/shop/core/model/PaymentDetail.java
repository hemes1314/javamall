package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.CurrencyUtil;

public class PaymentDetail {
	
	private Integer detail_id;
	private Integer payment_id;
	private Double pay_money;
	private long pay_date;
	private String admin_user;
	
	@PrimaryKeyField
	public Integer getDetail_id() {
        return detail_id;
    }
    public void setDetail_id(Integer detail_id) {
        this.detail_id = detail_id;
    }
    
	
	public Integer getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
	}
	public Double getPay_money() {
	    if(pay_money!=null){
	        pay_money=CurrencyUtil.round(pay_money, 2);
        }else{
            return 0.00d;
        }
        return pay_money;
	}
	public void setPay_money(Double pay_money) {
		this.pay_money = pay_money;
	}
	public long getPay_date() {
		return pay_date;
	}
	public void setPay_date(long pay_date) {
		this.pay_date = pay_date;
	}
	public String getAdmin_user() {
		return admin_user;
	}
	public void setAdmin_user(String admin_user) {
		this.admin_user = admin_user;
	}
	

}
