package com.enation.app.shop.core.model;

import java.math.BigDecimal;

import com.enation.framework.database.PrimaryKeyField;

public class SommelierMyType {

	private long id;
	private int sommelier_id;
	private long order_type_id;
	private String order_type_name;
	private int status;
	private double order_type_price;
	private double my_price;
	private String order_type_info;

	
	@PrimaryKeyField

    
    public int getSommelier_id() {
        return sommelier_id;
    }
    
    
    public long getId() {
        return id;
    }

    
    public void setId(long id) {
        this.id = id;
    }

    public void setSommelier_id(int sommelier_id) {
        this.sommelier_id = sommelier_id;
    }
     
    public void setOrder_type_id(int order_type_id) {
        this.order_type_id = order_type_id;
    }
    

    
    public void setMy_price(Double my_price) {
        this.my_price = my_price;
    }
    
    public String getOrder_type_name() {
        return order_type_name;
    }
    
    public void setOrder_type_name(String order_type_name) {
        this.order_type_name = order_type_name;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public double getOrder_type_price() {
        return order_type_price;
    }
    
    public void setOrder_type_price(double order_type_price) {
        this.order_type_price = order_type_price;
    }


    
    public double getMy_price() {
        return my_price;
    }


    
    
    public long getOrder_type_id() {
        return order_type_id;
    }


    
    public void setOrder_type_id(long order_type_id) {
        this.order_type_id = order_type_id;
    }


    public void setMy_price(double my_price) {
        this.my_price = my_price;
    }


    
    public String getOrder_type_info() {
        return order_type_info;
    }


    
    public void setOrder_type_info(String order_type_info) {
        this.order_type_info = order_type_info;
    }
    
}
