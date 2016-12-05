package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 预存款日志
 * 
 * @author lzf<br/>
 *         2010-4-14上午09:41:36<br/>
 *         version 1.0
 */
@SuppressWarnings("serial")
public class AdvanceLogs implements java.io.Serializable {
	private int log_id;
	private long member_id;
	private Double money;
	private String message;
	private Long mtime;
	private String payment_id;
	private String order_id;
	private String paymethod;
	private String memo;
	private Double import_advance;
	private Double import_virtual;
	private Double export_advance;
	private Double export_virtual;
	
	private Double member_advance;
	private Double member_virtual;
	private Double shop_advance;
	private String disabled;// enum('true','false') NOT NULL DEFAULT 'false'

	@PrimaryKeyField
	public int getLog_id() {
		return log_id;
	}

	public void setLog_id(int logId) {
		log_id = logId;
	}

	public long getMember_id() {
		return member_id;
	}

	public void setMember_id(long memberId) {
		member_id = memberId;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getMtime() {
		return mtime;
	}

	public void setMtime(Long mtime) {
		this.mtime = mtime;
	}

	public String getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getPaymethod() {
		return paymethod;
	}

	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	

	public Double getMember_advance() {
		return member_advance;
	}

	public void setMember_advance(Double memberAdvance) {
		member_advance = memberAdvance;
	}

	public Double getShop_advance() {
		return shop_advance;
	}

	public void setShop_advance(Double shopAdvance) {
		shop_advance = shopAdvance;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

    public Double getImport_advance() {
        return import_advance;
    }

    public void setImport_advance(Double import_advance) {
        this.import_advance = import_advance;
    }

    public Double getImport_virtual() {
        return import_virtual;
    }

    public void setImport_virtual(Double import_virtual) {
        this.import_virtual = import_virtual;
    }

    public Double getExport_advance() {
        return export_advance;
    }

    public void setExport_advance(Double export_advance) {
        this.export_advance = export_advance;
    }

    public Double getExport_virtual() {
        return export_virtual;
    }

    public void setExport_virtual(Double export_virtual) {
        this.export_virtual = export_virtual;
    }

    public Double getMember_virtual() {
        return member_virtual;
    }

    public void setMember_virtual(Double member_virtual) {
        this.member_virtual = member_virtual;
    }

   

}
