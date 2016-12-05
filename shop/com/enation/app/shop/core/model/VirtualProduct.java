package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class VirtualProduct {

	private int id;
	private String name;
	private String intro;
	private Float price;
	private String images;
	   
	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIntro() {
        return intro;
    }
    
    public void setIntro(String intro) {
        this.intro = intro;
    }
    public String getImages() {
        return images;
    }
    
    public void setImages(String images) {
        this.images = images;
    }

}
