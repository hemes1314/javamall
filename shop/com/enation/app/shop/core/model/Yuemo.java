package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class Yuemo {

	private int id;
	private String title;
	private String content;
	private Float price;
	private String member;
	private int  length;
	private String  time;
	private String  address;
	private int  plimit;
	private int  status;
	private String  image;
	private String  brief;
	
	   
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    public Yuemo(){
	       this.length = 0;  
	   };
	   public Yuemo(String title, String content){
	       this.title = title;
	       this.content = content;
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
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	public int getLength() {
	        return length;
    }
    public void setLength(int length) {
	        this.length = length;
   }
    public String getTime() {
        return time;
    }
    public  void setTime(String time) {
        this.time = time;
    }

    
    public int getPlimit() {
        return plimit;
    }

    
    public void setPlimit(int plimit) {
        this.plimit = plimit;
    }

    
    public int getStatus() {
        return status;
    }

    
    public void setStatus(int status) {
        this.status = status;
    }

    
    public String getImage() {
        return image;
    }

    
    public void setImage(String image) {
        this.image = image;
    }

    
    public String getBrief() {
        return brief;
    }

    
    public void setBrief(String brief) {
        this.brief = brief;
    }
    
  
}
