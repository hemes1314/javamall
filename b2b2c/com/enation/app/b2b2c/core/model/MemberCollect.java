package com.enation.app.b2b2c.core.model;

import java.io.Serializable;

/**
 * 收藏店铺表
 * @author xulipeng
 *
 */
public class MemberCollect implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3611072385708540242L;
	
	private Integer id;
	private Long member_id;
	private Integer store_id;
	private Long create_time;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getMember_id() {
		return member_id;
	}
	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
}
