package com.enation.app.shop.core.model;

import java.math.BigDecimal;

import com.enation.framework.database.PrimaryKeyField;

public class SommelierCDayOrder {

	private int id;
	private int sommelier_id;
	private String sommelier_name;
	private String sommelier_face;
	private String orderTimeSection;

	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
    
    public int getSommelier_id() {
        return sommelier_id;
    }
    
    public void setSommelier_id(int sommelier_id) {
        this.sommelier_id = sommelier_id;
    }
    
    public String getOrderTimeSection() {
        return orderTimeSection;
    }
    
    public void setOrderTimeSection(String orderTimeSection) {
        this.orderTimeSection = orderTimeSection;
    }
    
    public String getSommelier_name() {
        return sommelier_name;
    }
    
    public void setSommelier_name(String sommelier_name) {
        this.sommelier_name = sommelier_name;
    }
    
    public String getSommelier_face() {
        return sommelier_face;
    }
    
    public void setSommelier_face(String sommelier_face) {
        this.sommelier_face = sommelier_face;
    }

    
}
