package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class Cf {

	private int id;
	private String title;
	private String content;
	private float price;
	private String member;
	private int  length;
	private String  time;
	private String  target;
	private String  cffrom;
	private String  status;
	private String  bimage;
	private String  iimage;
	private String  release_time;
	private String  detail_image;
	private Float  already_get;
	private String  release_nickname;
	private String  release_face;
	private String  subheading;
	private String  brief;
	private int  click;
	private Integer  support;
	
	   public Cf(){
	       this.length = 0;  
	   };
	   public Cf(String title, String content){
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
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getCffrom() {
        return cffrom;
    }
    
    public void setCffrom(String cffrom) {
        this.cffrom = cffrom;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getBimage() {
        return bimage;
    }
    
    public void setBimage(String bimage) {
        this.bimage = bimage;
    }
    
    public String getIimage() {
        return iimage;
    }
    
    public void setIimage(String iimage) {
        this.iimage = iimage;
    }
    
    public String getRelease_time() {
        return release_time;
    }
    
    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }
    
    public String getDetail_image() {
        return detail_image;
    }
    
    public void setDetail_image(String detail_image) {
        this.detail_image = detail_image;
    }
    
    public Float getAlready_get() {
        return already_get;
    }
    
    public void setAlready_get(Float already_get) {
        this.already_get = already_get;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public String getRelease_nickname() {
        return release_nickname;
    }
    
    public void setRelease_nickname(String release_nickname) {
        this.release_nickname = release_nickname;
    }
    
    public String getRelease_face() {
        return release_face;
    }
    
    public void setRelease_face(String release_face) {
        this.release_face = release_face;
    }
    
    public String getSubheading() {
        return subheading;
    }
    
    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }
    
    public String getBrief() {
        return brief;
    }
    
    public void setBrief(String brief) {
        this.brief = brief;
    }
    
    public int getClick() {
        return click;
    }
    
    public void setClick(int click) {
        this.click = click;
    }
    
    public Integer getSupport() {
        return support;
    }
    
    public void setSupport(Integer support) {
        this.support = support;
    }
    

    
}
