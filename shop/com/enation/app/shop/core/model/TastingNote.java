package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class TastingNote {

	private int id;
	private String title;
	private String introduce;
	private String detail;
	private String userid;
	private String good_id;
	private String good_name;
	private String sommelierid;
	private String year;
	private Integer star;
	private String image;
	private float price;

	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    
    public String getIntroduce() {
        return introduce;
    }
    
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
      
    
    public String getDetail() {
        return detail;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getUserid() {
        return userid;
    }
    
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    public String getGood_id() {
        return good_id;
    }
    
    public void setGood_id(String good_id) {
        this.good_id = good_id;
    }
    
    public String getSommelierid() {
        return sommelierid;
    }
    
    public void setSommelierid(String sommelierid) {
        this.sommelierid = sommelierid;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public Integer getStar() {
        return star;
    }
    
    public void setStar(Integer star) {
        this.star = star;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public String getGood_name() {
        return good_name;
    }
    
    public void setGood_name(String good_name) {
        this.good_name = good_name;
    }

    
}
