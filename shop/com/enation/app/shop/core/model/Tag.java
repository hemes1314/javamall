package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @author lzf
 * version 1.0<br/>
 * 2010-6-17&nbsp;下午02:31:00
 */
public class Tag implements java.io.Serializable{
	
	private Integer tag_id;
	private String tag_name;
	private Integer rel_count;
	private String is_key_select;
	@PrimaryKeyField
	public Integer getTag_id() {
		return tag_id;
	}
	public void setTag_id(Integer tagId) {
		tag_id = tagId;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tagName) {
		tag_name = tagName;
	}
	public Integer getRel_count() {
		return rel_count;
	}
	public void setRel_count(Integer relCount) {
		rel_count = relCount;
	}
    
    public String getIs_key_select() {
        return is_key_select;
    }
    
    public void setIs_key_select(String is_key_select) {
        this.is_key_select = is_key_select;
    }
	
}
