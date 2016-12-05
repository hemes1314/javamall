package com.enation.app.shop.core.model;

/**
 * 会员礼品卡
 * @author 2015/10/16 humaodong
 *
 */

import com.enation.framework.database.DynamicField;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

@SuppressWarnings("serial")
public class MemberGiftcard extends DynamicField implements java.io.Serializable,PayEnable {
    
    private Integer card_id;
    private long member_id;
    private String type_name;
    private String type_image;
    private int money;
    private Double price;
    private String card_sn;
    private String card_pw;
    private Long create_time;
    private int payment_id;
    private int used;
    private Long used_time;
    private Long used_member_id;
    
    @PrimaryKeyField
    public Integer getCard_id() {
        return card_id;
    }
    public void setCard_id(Integer card_id) {
        this.card_id = card_id;
    }
    public long getMember_id() {
        return member_id;
    }
    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }
    public String getType_name() {
        return type_name;
    }
    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
    public String getType_image() {
        return type_image;
    }
    public void setType_image(String type_image) {
        this.type_image = type_image;
    }
    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getCard_sn() {
        return card_sn;
    }
    public void setCard_sn(String card_sn) {
        this.card_sn = card_sn;
    }
    public String getCard_pw() {
        return card_pw;
    }
    public void setCard_pw(String card_pw) {
        this.card_pw = card_pw;
    }
    public Long getCreate_time() {
        return create_time;
    }
    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }
    public int getPayment_id() {
        return payment_id;
    }
    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }
    public int getUsed() {
        return used;
    }
    public void setUsed(int used) {
        this.used = used;
    }
    public Long getUsed_time() {
        return used_time;
    }
    public void setUsed_time(Long used_time) {
        this.used_time = used_time;
    }
    public Long getUsed_member_id() {
        return used_member_id;
    }
    public void setUsed_member_id(Long used_member_id) {
        this.used_member_id = used_member_id;
    }
    
    ///////////////////////////////////////////////////////
    
    @Override
    @NotDbField
    public Double getNeedPayMoney() {
        
        return price;
    }
    @Override
    @NotDbField
    public String getSn() {
        return card_sn;
    }
    @Override
    @NotDbField
    public String getOrderType() {
        return "giftcard";
    }
    
	@Override
	@NotDbField
	public String getTradeno() {
		return "";
	}
	
	@Override
	@NotDbField
	public String getRefund_batchno() {
		return "";
	}
	
	@Override
	public Integer getOrder_id() {
		return null;
	}
	@Override
	public Integer getParent_id() {
		return null;
	}
}
