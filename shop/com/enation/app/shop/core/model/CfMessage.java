package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class CfMessage  implements java.io.Serializable  {
    
    private int id;
    private Integer cf_id;
    private String member_id;
    private String message;
    private String if_join;
    private String time;
	
    public String getIf_join() {
        return if_join;
    }
    
    public void setIf_join(String if_join) {
        this.if_join = if_join;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }

	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
    
    public Integer getCf_id() {
        return cf_id;
    }
    
    public void setCf_id(Integer cf_id) {
        this.cf_id = cf_id;
    }
    
    public String getMember_id() {
        return member_id;
    }
    
    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    
}
