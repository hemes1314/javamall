package com.enation.app.base.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;


/**
 * smpt
 * @author kingapex
 * @date 2011-11-1 下午12:07:07 
 * @version V1.0
 */
public class Smtp  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4645737054149076379L;
	private Integer id;
	private String host;
	private String username;
	private String password;
	private long last_send_time;
	private int send_count;
	private int max_count;
	private String mail_from;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public long getLast_send_time() {
		return last_send_time;
	}
	public void setLast_send_time(long last_send_time) {
		this.last_send_time = last_send_time;
	}
	public int getSend_count() {
		return send_count;
	}
	public void setSend_count(int send_count) {
		this.send_count = send_count;
	}
	public int getMax_count() {
		return max_count;
	}
	public void setMax_count(int max_count) {
		this.max_count = max_count;
	}
	public String getMail_from() {
		return mail_from;
	}
	public void setMail_from(String mail_from) {
		this.mail_from = mail_from;
	}
	
	
}
