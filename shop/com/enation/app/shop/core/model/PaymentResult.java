package com.enation.app.shop.core.model;

/**
 * 支付结果实体
 * @author kingapex
 *2013-9-5上午9:47:06
 */
public class PaymentResult {
	
	private int result;//支付结果，1表示成功，0表示失败
	private String ordertype; // 2015/11/5 humaodong
	private String ordersn;//订单号，如果支付成功此属性为订单号，否则为空。
	private String error;//支付失败的错误信息
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getOrdersn() {
		return ordersn;
	}
	public void setOrdersn(String ordersn) {
		this.ordersn = ordersn;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
    public String getOrdertype() {
        return ordertype;
    }
    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }
	
	
	
	
}
