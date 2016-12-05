package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class Maimo {

	private int id;
	private String member_id;
	private String title;
	private String type;
	private String content;
	private String address;
	private String price;
	private String contact;
	private String images;
	private int  status;
	private String face;
	private String uname;
	private String nickname;
   
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
	
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

    
    public String getContent() {
        return content;
    }

    
    public void setContent(String content) {
        this.content = content;
    }

    public String getPrice() {
        return price;
    }

    
    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    
    public void setStatus(int status) {
        this.status = status;
    }

    
    public String getMember_id() {
        return member_id;
    }

    
    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    
    public String getContact() {
        return contact;
    }

    
    public void setContact(String contact) {
        this.contact = contact;
    }

    
    public String getImages() {
        return images;
    }

    
    public void setImages(String images) {
        this.images = images;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getFace() {
        return face;
    }

    
    public void setFace(String face) {
        this.face = face;
    }

    
    public String getUname() {
        return uname;
    }

    
    public void setUname(String uname) {
        this.uname = uname;
    }

    
    public String getNickname() {
        return nickname;
    }

    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
}
