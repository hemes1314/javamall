package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class AuctionRecord {

	private int id;
	private int auction_id;
	private int status;
	private String userid;
	private String name;
	private String user_name;
	private String nickname;
	private String mobile;
	private String tel;
	private String address;
	private String price;
	private String time;


    public void AuctionRecord(){
 
    }


    @PrimaryKeyField
    public int getId() {
        return id;
    }


    
    public void setId(int id) {
        this.id = id;
    }


    
    public int getAuction_id() {
        return auction_id;
    }


    
    public void setAuction_id(int auction_id) {
        this.auction_id = auction_id;
    }


    
    public int getStatus() {
        return status;
    }


    
    public void setStatus(int status) {
        this.status = status;
    }


    
    public String getUserid() {
        return userid;
    }


    
    public void setUserid(String userid) {
        this.userid = userid;
    }


    
    public String getPrice() {
        return price;
    }


    
    public void setPrice(String price) {
        this.price = price;
    }


    
    public String getTime() {
        return time;
    }


    
    public void setTime(String time) {
        this.time = time;
    }

    

    
    public String getUser_name() {
        return user_name;
    }


    
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getNickname() {
        return nickname;
    }


    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    
    
    
    public String getMobile() {
        return mobile;
    }


    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    
    public String getName() {
        return name;
    }


    
    public void setName(String name) {
        this.name = name;
    }


    public String getTel() {
        return tel;
    }


    
    public void setTel(String tel) {
        this.tel = tel;
    }


    
    public String getAddress() {
        return address;
    }


    
    public void setAddress(String address) {
        this.address = address;
    }
    
	   
}
