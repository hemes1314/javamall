package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class UtastingNote {

	private int id;
    private String  member_id;
    private String  wine_name;
    private String  appearance1;
    private String  appearance2;
    private String appearance3;
    private String quality1;
    private String quality2;
    private String quality3;
    private String brand1;
    private String brand2;
    private String brand3; 
    private String price1;
    private String price2;
    private String price3;   
    private String appraise;
    private String score;
    private String fnappearanceVoice;
    private String fnqualityVoice;
    private String fnbrandVoice;
    private String fnpriceVoice;
    private String fnappraiseVoice;
    private String fnscoreVoice;
    
    private String fnimagea;
    private String fnimageb;
    private String fnimagec;
    private String fnimaged;
    private String fnimagee;
    private String fnimagef;
    private String fnimageg;
    private String fnimageh;
    private String fnimagei; 	
    
    private String release_time; 
	  
	@PrimaryKeyField
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
    
    public String getWine_name() {
        return wine_name;
    }
    
    public void setWine_name(String wine_name) {
        this.wine_name = wine_name;
    }
     
    public String getAppearance1() {
        return appearance1;
    }
    
    public void setAppearance1(String appearance1) {
        this.appearance1 = appearance1;
    }
    
    public String getAppearance2() {
        return appearance2;
    }
    
    public void setAppearance2(String appearance2) {
        this.appearance2 = appearance2;
    }
    
    public String getAppearance3() {
        return appearance3;
    }
    
    public void setAppearance3(String appearance3) {
        this.appearance3 = appearance3;
    }
    
    public String getQuality1() {
        return quality1;
    }
    
    public void setQuality1(String quality1) {
        this.quality1 = quality1;
    }
    
    public String getQuality2() {
        return quality2;
    }
    
    public void setQuality2(String quality2) {
        this.quality2 = quality2;
    }
    
    public String getQuality3() {
        return quality3;
    }
    
    public void setQuality3(String quality3) {
        this.quality3 = quality3;
    }
    
    public String getBrand1() {
        return brand1;
    }
    
    public void setBrand1(String brand1) {
        this.brand1 = brand1;
    }
    
    public String getBrand2() {
        return brand2;
    }
    
    public void setBrand2(String brand2) {
        this.brand2 = brand2;
    }
    
    public String getBrand3() {
        return brand3;
    }
    
    public void setBrand3(String brand3) {
        this.brand3 = brand3;
    }
    
    public String getPrice1() {
        return price1;
    }
    
    public void setPrice1(String price1) {
        this.price1 = price1;
    }
    
    public String getPrice2() {
        return price2;
    }
    
    public void setPrice2(String price2) {
        this.price2 = price2;
    }
    
    public String getPrice3() {
        return price3;
    }
    
    public void setPrice3(String price3) {
        this.price3 = price3;
    }
 
    public String getMember_id() {
        return member_id;
    }
    
    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }
    
    public String getAppraise() {
        return appraise;
    }
    
    public void setAppraise(String appraise) {
        this.appraise = appraise;
    }
    
    public String getScore() {
        return score;
    }
    
    public void setScore(String score) {
        this.score = score;
    }
    
    public String getFnappearanceVoice() {
        return fnappearanceVoice;
    }
    
    public void setFnappearanceVoice(String fnappearanceVoice) {
        this.fnappearanceVoice = fnappearanceVoice;
    }
    
    public String getFnqualityVoice() {
        return fnqualityVoice;
    }
    
    public void setFnqualityVoice(String fnqualityVoice) {
        this.fnqualityVoice = fnqualityVoice;
    }
    
    public String getFnbrandVoice() {
        return fnbrandVoice;
    }
    
    public void setFnbrandVoice(String fnbrandVoice) {
        this.fnbrandVoice = fnbrandVoice;
    }
    
    public String getFnpriceVoice() {
        return fnpriceVoice;
    }
    
    public void setFnpriceVoice(String fnpriceVoice) {
        this.fnpriceVoice = fnpriceVoice;
    }
    
    public String getFnappraiseVoice() {
        return fnappraiseVoice;
    }
    
    public void setFnappraiseVoice(String fnappraiseVoice) {
        this.fnappraiseVoice = fnappraiseVoice;
    }
    
    public String getFnscoreVoice() {
        return fnscoreVoice;
    }
    
    public void setFnscoreVoice(String fnscoreVoice) {
        this.fnscoreVoice = fnscoreVoice;
    }
    
    public String getFnimagea() {
        return fnimagea;
    }
    
    public void setFnimagea(String fnimagea) {
        this.fnimagea = fnimagea;
    }
    
    public String getFnimageb() {
        return fnimageb;
    }
    
    public void setFnimageb(String fnimageb) {
        this.fnimageb = fnimageb;
    }
    
    public String getFnimagec() {
        return fnimagec;
    }
    
    public void setFnimagec(String fnimagec) {
        this.fnimagec = fnimagec;
    }
    
    public String getFnimaged() {
        return fnimaged;
    }
    
    public void setFnimaged(String fnimaged) {
        this.fnimaged = fnimaged;
    }
    
    public String getFnimagee() {
        return fnimagee;
    }
    
    public void setFnimagee(String fnimagee) {
        this.fnimagee = fnimagee;
    }
    
    public String getFnimagef() {
        return fnimagef;
    }
    
    public void setFnimagef(String fnimagef) {
        this.fnimagef = fnimagef;
    }
    
    public String getFnimageg() {
        return fnimageg;
    }
    
    public void setFnimageg(String fnimageg) {
        this.fnimageg = fnimageg;
    }
    
    public String getFnimageh() {
        return fnimageh;
    }
    
    public void setFnimageh(String fnimageh) {
        this.fnimageh = fnimageh;
    }
    
    public String getFnimagei() {
        return fnimagei;
    }
    
    public void setFnimagei(String fnimagei) {
        this.fnimagei = fnimagei;
    }
    
    public String getRelease_time() {
        return release_time;
    }
    
    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }
    
    
}
