package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class Auction {

	private int id;
	private String title;
	private String goodsn;
	private String content;
	private String sprice;
	private String aprice;
	private String cprice;
	private int length;
    private String status;
	private String member;
	private String  time;
	private long  etime;
	private long  stime;
	private String  onlookers;
	private String  service;
	private String  start_time;
	private String  image;

    public void Yuemo(){
        this.length = 0;  
    };
	
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
	
    public String getSprice() {
        return sprice;
    }

    
    public void setSprice(String sprice) {
        this.sprice = sprice;
    }

    
    public String getAprice() {
        return aprice;
    }

    
    public void setAprice(String aprice) {
        this.aprice = aprice;
    }

    
    public String getCprice() {
        return cprice;
    }

    
    public void setCprice(String cprice) {
        this.cprice = cprice;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status = status;
    }

    public Auction(String title, String content){
	       this.title = title;
	       this.content = content;
	   }
	
	public Auction() {
        // TODO Auto-generated constructor stub
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

	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
    public String getTime() {
        return time;
    }
    public  void setTime(String time) {
        this.time = time;
    }

     
    
    public String getOnlookers() {
        return onlookers;
    }

    
    public void setOnlookers(String onlookers) {
        this.onlookers = onlookers;
    }

    public String getService() {
        return service;
    }

    
    public void setService(String service) {
        this.service = service;
    }

    
    public String getStart_time() {
        return start_time;
    }

    
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    
    public String getImage() {
        return image;
    }

    
    public void setImage(String image) {
        this.image = image;
    }

    
    public String getGoodsn() {
        return goodsn;
    }

    
    public void setGoodsn(String goodsn) {
        this.goodsn = goodsn;
    }

    
    public long getEtime() {
        return etime;
    }

    
    public void setEtime(long etime) {
        this.etime = etime;
    }

    
    public long getStime() {
        return stime;
    }

    
    public void setStime(long stime) {
        this.stime = stime;
    }
       
}
