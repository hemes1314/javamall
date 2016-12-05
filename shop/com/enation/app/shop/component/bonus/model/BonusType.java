package com.enation.app.shop.component.bonus.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 红包类型实体
 * 
 * @author kingapex 2013-8-13下午2:39:27
 */
public class BonusType {

	private int type_id;
	private String type_name;
	private Double type_money;
	private int send_type;
	private Long send_start_date;
	private Long send_end_date;
	private Long use_start_date;
	private Long use_end_date;
	//TODO:Jeffrey 由于红包的发放时候 值为null，查不出红包，暂时默认为0
	private Double min_goods_amount = 0D;
	private String recognition;
	private int create_num;
	private int use_num;
	
	//2015/10/13 humaodong
	private String store_name;
	
	/** 新增字段 xlp 2015年08月04日14:31:12 */
	private int belong;			//优惠券归属，1：平台，2：商家
	
	
	@PrimaryKeyField
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public Double getType_money() {
		return type_money;
	}
	public void setType_money(Double type_money) {
		this.type_money = type_money;
	}
	public int getSend_type() {
		return send_type;
	}
	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public void setMin_goods_amount(Double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}
	public String getRecognition() {
		return recognition;
	}
	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
	public int getCreate_num() {
		return create_num;
	}
	public void setCreate_num(int create_num) {
		this.create_num = create_num;
	}
	public int getUse_num() {
		return use_num;
	}
	public void setUse_num(int use_num) {
		this.use_num = use_num;
	}
	public Long getSend_start_date() {
		return send_start_date;
	}
	public void setSend_start_date(Long send_start_date) {
		this.send_start_date = send_start_date;
	}
	public Long getSend_end_date() {
		return send_end_date;
	}
	public void setSend_end_date(Long send_end_date) {
		this.send_end_date = send_end_date;
	}
	public Long getUse_start_date() {
		return use_start_date;
	}
	public void setUse_start_date(Long use_start_date) {
		this.use_start_date = use_start_date;
	}
	public Long getUse_end_date() {
		return use_end_date;
	}
	public void setUse_end_date(Long use_end_date) {
		this.use_end_date = use_end_date;
	}
	public Double getMin_goods_amount() {
		return min_goods_amount;
	}
	public int getBelong() {
		return belong;
	}
	public void setBelong(int belong) {
		this.belong = belong;
	}
	
	@NotDbField
    public String getStore_name() {
        return store_name;
    }
    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }
}
