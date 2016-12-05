package com.enation.app.shop.core.model;

import java.math.BigDecimal;

import com.enation.framework.database.PrimaryKeyField;

public class SommelierOrder {

	private int id;
	private int typeid;
	private int typename;
	private int sommelier_id;
	private int sommelier_name;
	private int memid;
	private long stime;
	private long etime;
	private int status;
	private String name;
	private String address;
	private String area;
	private String zipcode;
	private String mobile;
	private String remark;
	
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
    
    public long getStime() {
        return stime;
    }
    
    public void setStime(long stime) {
        this.stime = stime;
    }
    
    public long getEtime() {
        return etime;
    }
    
    public void setEtime(long etime) {
        this.etime = etime;
    }
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    

    public int getMemid() {
        return memid;
    }
    
    public void setMemid(int memid) {
        this.memid = memid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getArea() {
        return area;
    }
    
    public void setArea(String area) {
        this.area = area;
    }
    
    public String getZipcode() {
        return zipcode;
    }
    
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public int getSommelier_name() {
        return sommelier_name;
    }
    
    public void setSommelier_name(int sommelier_name) {
        this.sommelier_name = sommelier_name;
    }
    
    public int getTypeid() {
        return typeid;
    }
    
    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }
    
    public int getTypename() {
        return typename;
    }
    
    public void setTypename(int typename) {
        this.typename = typename;
    }
     
    
}
